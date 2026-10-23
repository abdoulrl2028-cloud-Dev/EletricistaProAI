package com.eletricistapro.ai.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.eletricistapro.ai.R
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ToolScaffold(title: String, onBack: (() -> Unit)?, content: @Composable (PaddingValues) -> Unit) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(title) },
                navigationIcon = {
                    if (onBack != null) {
                        TextButton(onClick = onBack) { Text(stringResource(R.string.back)) }
                    }
                },
            )
        },
        content = content,
    )
}

@Composable
fun ScrollColumn(padding: PaddingValues, content: @Composable ColumnScope.() -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(padding)
            .verticalScroll(rememberScrollState())
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        content = content,
    )
}

@Composable
fun NumberField(label: String, value: String, onValue: (String) -> Unit) {
    OutlinedTextField(
        value = value,
        onValueChange = onValue,
        label = { Text(label) },
        modifier = Modifier.fillMaxWidth(),
        singleLine = true,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
    )
}

@Composable
fun SectionCard(title: String, body: String, onClick: () -> Unit) {
    Card(onClick = onClick, modifier = Modifier.fillMaxWidth()) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(title, style = MaterialTheme.typography.titleMedium)
            Text(body, style = MaterialTheme.typography.bodyMedium)
        }
    }
}

@Composable
fun ResultBlock(formula: String, value: String) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Text(stringResource(R.string.result), style = MaterialTheme.typography.titleMedium)
            Text(formula, fontFamily = FontFamily.Monospace)
            Text(value)
        }
    }
}

@Composable
fun PrimaryButton(label: String, onClick: () -> Unit) {
    Button(onClick = onClick, modifier = Modifier.fillMaxWidth()) {
        Text(label)
    }
}

@Composable
fun ChoiceRow(labels: List<Pair<Boolean, String>>, onSelect: (Int) -> Unit) {
    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        labels.forEachIndexed { index, (selected, label) ->
            Button(onClick = { onSelect(index) }, enabled = !selected) {
                Text(label)
            }
        }
    }
}

fun parseNumber(text: String): Double? {
    if (text.isBlank()) return null
    return text.trim().replace(" ", "").replace(',', '.').toDoubleOrNull()
}

fun formatQuantity(value: Double): String {
    return DecimalFormat("#,##0.###", DecimalFormatSymbols.getInstance()).format(value)
}
