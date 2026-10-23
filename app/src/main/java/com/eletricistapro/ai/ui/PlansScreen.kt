package com.eletricistapro.ai.ui

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import com.eletricistapro.ai.R
import com.eletricistapro.ai.data.ContentRepository
import com.eletricistapro.ai.data.PlanCatalog
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.text.NumberFormat
import java.util.Currency
import java.util.Locale

@Composable
fun PlansScreen(onBack: () -> Unit) {
    val context = LocalContext.current
    var catalog by remember { mutableStateOf<PlanCatalog?>(null) }
    var failed by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        val loaded = withContext(Dispatchers.IO) {
            runCatching { ContentRepository.loadPlans(context) }.getOrNull()
        }
        if (loaded == null) failed = true else catalog = loaded
    }
    ToolScaffold(stringResource(R.string.section_plans), onBack) { padding ->
        ScrollColumn(padding) {
            Text(stringResource(R.string.plans_purchase_disabled))
            when {
                failed -> Text(stringResource(R.string.plans_file_error), color = MaterialTheme.colorScheme.error)
                catalog == null -> Text(stringResource(R.string.loading))
                else -> {
                    Text(stringResource(R.string.updated_at, catalog!!.updatedAt))
                    catalog!!.plans.forEach { plan ->
                        val price = plan.prices.firstOrNull()
                        val priceText = if (price == null) {
                            stringResource(R.string.no_price)
                        } else {
                            val formatted = formatMoney(price.currency, price.amount)
                            val period = if (price.period == "year") {
                                stringResource(R.string.per_year)
                            } else {
                                stringResource(R.string.per_month)
                            }
                            val labeled = "$formatted $period"
                            if (price.from) stringResource(R.string.price_from, labeled) else labeled
                        }
                        SectionCard(plan.name, priceText) {}
                        plan.features.forEach { feature -> Text("• $feature") }
                        Text(plan.conditions, style = MaterialTheme.typography.bodySmall)
                        Text(stringResource(R.string.test_price), style = MaterialTheme.typography.labelMedium)
                    }
                }
            }
        }
    }
}

private fun formatMoney(currency: String, amount: Double): String {
    val locale = if (currency == "BRL") Locale("pt", "BR") else Locale.getDefault()
    val format = NumberFormat.getCurrencyInstance(locale)
    runCatching { format.currency = Currency.getInstance(currency) }
    return format.format(amount)
}
