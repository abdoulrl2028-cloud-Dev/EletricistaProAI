package com.eletricistapro.ai.ui

import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.selection.selectable
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import com.eletricistapro.ai.R
import com.eletricistapro.ai.data.ContentRepository
import com.eletricistapro.ai.data.Lesson
import com.eletricistapro.ai.data.ProgressStore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@Composable
fun CourseScreen(onBack: () -> Unit) {
    val context = LocalContext.current
    var lesson by remember { mutableStateOf<Lesson?>(null) }
    var failed by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        val loaded = withContext(Dispatchers.IO) {
            runCatching { ContentRepository.loadLesson(context) }.getOrNull()
        }
        if (loaded == null) failed = true else lesson = loaded
    }
    ToolScaffold(stringResource(R.string.section_training), onBack) { padding ->
        ScrollColumn(padding) {
            when {
                failed -> Text(stringResource(R.string.course_missing))
                lesson == null -> Text(stringResource(R.string.loading))
                else -> LessonBody(lesson!!)
            }
        }
    }
}

@Composable
private fun LessonBody(lesson: Lesson) {
    val context = LocalContext.current
    val store = remember { ProgressStore(context) }
    var complete by remember { mutableStateOf(store.isLessonComplete(lesson.id)) }
    val answers = remember(lesson.id) { MutableList(lesson.questions.size) { -1 } }
    var revision by remember { mutableIntStateOf(0) }
    var graded by remember { mutableStateOf(false) }
    var score by remember { mutableIntStateOf(0) }
    Text(lesson.title, style = MaterialTheme.typography.headlineSmall)
    Text(lesson.summary)
    Text(stringResource(R.string.course_language_note), style = MaterialTheme.typography.bodySmall)
    if (complete) {
        Text(stringResource(R.string.lesson_complete), color = MaterialTheme.colorScheme.secondary)
    }
    lesson.blocks.forEach { block ->
        Text(block.heading, style = MaterialTheme.typography.titleMedium)
        Text(block.body)
    }
    lesson.questions.forEachIndexed { questionIndex, question ->
        Text(question.prompt, style = MaterialTheme.typography.titleSmall)
        question.choices.forEachIndexed { choiceIndex, choice ->
            val selected = answers[questionIndex] == choiceIndex && revision >= 0
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .selectable(selected = selected, role = Role.RadioButton) {
                        answers[questionIndex] = choiceIndex
                        revision++
                    },
            ) {
                androidx.compose.foundation.layout.Row(
                    Modifier.padding(8.dp),
                    verticalAlignment = androidx.compose.ui.Alignment.CenterVertically,
                ) {
                    RadioButton(selected = selected, onClick = null)
                    Text(choice, modifier = Modifier.padding(start = 8.dp))
                }
            }
        }
        if (graded) {
            val right = answers[questionIndex] == question.correctIndex
            Text(
                stringResource(if (right) R.string.answer_correct else R.string.answer_wrong) + " — " + question.explanation,
                color = if (right) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.error,
            )
        }
    }
    val needAll = stringResource(R.string.err_answer_all)
    PrimaryButton(stringResource(R.string.check_answers)) {
        if (answers.any { it < 0 }) {
            Toast.makeText(context, needAll, Toast.LENGTH_SHORT).show()
        } else {
            score = lesson.questions.indices.count { answers[it] == lesson.questions[it].correctIndex }
            graded = true
            if (score == lesson.questions.size) {
                store.markLessonComplete(lesson.id)
                complete = true
            }
        }
    }
    if (graded) {
        Text(stringResource(R.string.lesson_score, score, lesson.questions.size))
    }
    Text(stringResource(R.string.not_a_certificate), style = MaterialTheme.typography.bodySmall)
    Column(Modifier) {}
}
