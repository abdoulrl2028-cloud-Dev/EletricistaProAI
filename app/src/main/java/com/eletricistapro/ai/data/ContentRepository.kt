package com.eletricistapro.ai.data

import android.content.Context
import org.json.JSONObject
import java.util.Locale

data class LessonBlock(val heading: String, val body: String)

data class LessonQuestion(
    val prompt: String,
    val choices: List<String>,
    val correctIndex: Int,
    val explanation: String,
)

data class Lesson(
    val id: String,
    val title: String,
    val summary: String,
    val blocks: List<LessonBlock>,
    val questions: List<LessonQuestion>,
)

data class PlanPrice(
    val currency: String,
    val amount: Double,
    val period: String,
    val from: Boolean,
)

data class CommercialPlan(
    val id: String,
    val name: String,
    val prices: List<PlanPrice>,
    val features: List<String>,
    val conditions: String,
)

data class PlanCatalog(
    val updatedAt: String,
    val plans: List<CommercialPlan>,
)

object ContentRepository {
    fun loadLesson(context: Context): Lesson {
        val json = JSONObject(context.assets.open("courses/iniciante/ohm.json").bufferedReader().use { it.readText() })
        val blocks = json.getJSONArray("blocks")
        val questions = json.getJSONArray("questions")
        return Lesson(
            id = json.getString("id"),
            title = json.getString("title"),
            summary = json.getString("summary"),
            blocks = List(blocks.length()) { index ->
                val block = blocks.getJSONObject(index)
                LessonBlock(block.getString("heading"), block.getString("body"))
            },
            questions = List(questions.length()) { index ->
                val question = questions.getJSONObject(index)
                val choices = question.getJSONArray("choices")
                LessonQuestion(
                    prompt = question.getString("prompt"),
                    choices = List(choices.length()) { choices.getString(it) },
                    correctIndex = question.getInt("correctIndex"),
                    explanation = question.getString("explanation"),
                )
            },
        )
    }

    fun loadPlans(context: Context): PlanCatalog {
        val json = JSONObject(context.assets.open("plans.json").bufferedReader().use { it.readText() })
        val plans = json.getJSONArray("plans")
        val language = languageKey()
        return PlanCatalog(
            updatedAt = json.getString("updatedAt"),
            plans = List(plans.length()) { index ->
                val plan = plans.getJSONObject(index)
                val prices = plan.getJSONArray("prices")
                CommercialPlan(
                    id = plan.getString("id"),
                    name = localized(plan.getJSONObject("names"), language),
                    prices = List(prices.length()) { priceIndex ->
                        val price = prices.getJSONObject(priceIndex)
                        PlanPrice(
                            currency = price.getString("currency"),
                            amount = price.getDouble("amount"),
                            period = price.getString("period"),
                            from = price.optBoolean("from", false),
                        )
                    },
                    features = stringList(plan.getJSONObject("features"), language),
                    conditions = localized(plan.getJSONObject("conditions"), language),
                )
            },
        )
    }

    private fun languageKey(): String {
        return when (Locale.getDefault().language) {
            "en" -> "en"
            "fr" -> "fr"
            else -> "pt-BR"
        }
    }

    private fun localized(obj: JSONObject, language: String): String {
        return obj.optString(language, obj.getString("pt-BR"))
    }

    private fun stringList(obj: JSONObject, language: String): List<String> {
        val array = if (obj.has(language)) obj.getJSONArray(language) else obj.getJSONArray("pt-BR")
        return List(array.length()) { array.getString(it) }
    }
}

class ProgressStore(context: Context) {
    private val prefs = context.getSharedPreferences(MainActivityPrefs.NAME, Context.MODE_PRIVATE)

    fun isLessonComplete(id: String): Boolean = prefs.getBoolean(lessonKey(id), false)

    fun markLessonComplete(id: String) {
        prefs.edit().putBoolean(lessonKey(id), true).apply()
    }

    private fun lessonKey(id: String) = "lesson_$id"
}

object MainActivityPrefs {
    const val NAME = "progress"
}
