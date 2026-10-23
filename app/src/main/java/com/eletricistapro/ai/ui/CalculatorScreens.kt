package com.eletricistapro.ai.ui

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.res.stringResource
import com.eletricistapro.ai.R
import com.eletricistapro.calculators.CurrentEstimate
import com.eletricistapro.calculators.DcPower
import com.eletricistapro.calculators.EnergyCost
import com.eletricistapro.calculators.EstimateResult
import com.eletricistapro.calculators.FormulaId
import com.eletricistapro.calculators.OhmError
import com.eletricistapro.calculators.OhmResult
import com.eletricistapro.calculators.OhmsLaw
import com.eletricistapro.calculators.PowerResult
import com.eletricistapro.calculators.SupplySystem

@Composable
fun CalculatorHub(onBack: () -> Unit, onOpen: (String) -> Unit) {
    ToolScaffold(stringResource(R.string.section_calculators), onBack) { padding ->
        ScrollColumn(padding) {
            Text(stringResource(R.string.disclaimer_short), style = MaterialTheme.typography.bodySmall)
            SectionCard(stringResource(R.string.open_ohm), stringResource(R.string.ohm_help)) { onOpen("ohm") }
            SectionCard(stringResource(R.string.open_power), stringResource(R.string.estimated_current_help)) { onOpen("power") }
            SectionCard(stringResource(R.string.open_energy), stringResource(R.string.energy_help)) { onOpen("energy") }
        }
    }
}

@Composable
fun OhmScreen(onBack: () -> Unit) {
    var voltage by rememberSaveable { mutableStateOf("") }
    var current by rememberSaveable { mutableStateOf("") }
    var resistance by rememberSaveable { mutableStateOf("") }
    var message by rememberSaveable { mutableStateOf("") }
    var formula by rememberSaveable { mutableStateOf("") }
    val solvedTemplate = stringResource(R.string.solved_ohm)
    ToolScaffold(stringResource(R.string.open_ohm), onBack) { padding ->
        ScrollColumn(padding) {
            Text(stringResource(R.string.ohm_help))
            NumberField(stringResource(R.string.voltage), voltage) { voltage = it }
            NumberField(stringResource(R.string.current), current) { current = it }
            NumberField(stringResource(R.string.resistance), resistance) { resistance = it }
            PrimaryButton(stringResource(R.string.calculate)) {
                val parsed = parseOptional(voltage, current, resistance)
                if (parsed == null) {
                    formula = ""
                    message = "invalid"
                } else {
                    when (val result = OhmsLaw.solve(parsed[0], parsed[1], parsed[2])) {
                        is OhmResult.Solved -> {
                            formula = result.formula.name
                            message = "ok|${result.voltageVolts}|${result.currentAmperes}|${result.resistanceOhms}"
                        }
                        is OhmResult.Invalid -> {
                            formula = ""
                            message = result.error.name
                        }
                    }
                }
            }
            OhmOutcome(message, formula, solvedTemplate)
            Text(stringResource(R.string.disclaimer_short), style = MaterialTheme.typography.bodySmall)
        }
    }
}

@Composable
private fun OhmOutcome(message: String, formula: String, solvedTemplate: String) {
    if (message.isEmpty()) return
    if (message.startsWith("ok|")) {
        val parts = message.split("|")
        ResultBlock(
            formulaLabel(formula),
            solvedTemplate.format(
                formatQuantity(parts[1].toDouble()),
                formatQuantity(parts[2].toDouble()),
                formatQuantity(parts[3].toDouble()),
            ),
        )
        return
    }
    val error = when (message) {
        OhmError.NEGATIVE_OR_NON_FINITE.name -> R.string.err_negative
        OhmError.ZERO_RESISTANCE.name -> R.string.err_zero_r
        OhmError.ZERO_CURRENT.name -> R.string.err_zero_i
        OhmError.INCONSISTENT.name -> R.string.err_inconsistent
        "invalid" -> R.string.err_invalid_number
        else -> R.string.err_need_two
    }
    Text(stringResource(error), color = MaterialTheme.colorScheme.error)
}

@Composable
fun PowerScreen(onBack: () -> Unit) {
    var voltage by rememberSaveable { mutableStateOf("") }
    var current by rememberSaveable { mutableStateOf("") }
    var power by rememberSaveable { mutableStateOf("") }
    var factor by rememberSaveable { mutableStateOf("1") }
    var threePhase by rememberSaveable { mutableStateOf(false) }
    var outcome by rememberSaveable { mutableStateOf("") }
    ToolScaffold(stringResource(R.string.open_power), onBack) { padding ->
        ScrollColumn(padding) {
            Text(stringResource(R.string.dc_power_help))
            NumberField(stringResource(R.string.voltage), voltage) { voltage = it }
            NumberField(stringResource(R.string.current), current) { current = it }
            PrimaryButton(stringResource(R.string.calculate) + " · P") {
                val volts = parseNumber(voltage)
                val amperes = parseNumber(current)
                outcome = if (voltage.isBlank() || current.isBlank() || volts == null || amperes == null) {
                    "invalid"
                } else when (val result = DcPower.watts(volts, amperes)) {
                    is PowerResult.Ok -> "power|${result.watts}"
                    PowerResult.Invalid -> "bad"
                }
            }
            Text(stringResource(R.string.estimated_current_help))
            NumberField(stringResource(R.string.power), power) { power = it }
            NumberField(stringResource(R.string.power_factor), factor) { factor = it }
            ChoiceRow(
                listOf(!threePhase to stringResource(R.string.single_phase), threePhase to stringResource(R.string.three_phase)),
            ) { threePhase = it == 1 }
            PrimaryButton(stringResource(R.string.calculate) + " · I") {
                val watts = parseNumber(power)
                val volts = parseNumber(voltage)
                val pf = parseNumber(factor)
                outcome = if (power.isBlank() || voltage.isBlank() || factor.isBlank() || watts == null || volts == null || pf == null) {
                    "invalid"
                } else when (
                    val result = CurrentEstimate.amperes(
                        watts,
                        volts,
                        pf,
                        if (threePhase) SupplySystem.THREE_PHASE_LINE_VOLTAGE else SupplySystem.SINGLE_PHASE,
                    )
                ) {
                    is EstimateResult.Ok -> "current|${result.amperes}|${result.formula.name}"
                    EstimateResult.Invalid -> "bad"
                }
            }
            PowerOutcome(outcome)
            Text(stringResource(R.string.disclaimer_short), style = MaterialTheme.typography.bodySmall)
        }
    }
}

@Composable
private fun PowerOutcome(outcome: String) {
    when {
        outcome.startsWith("power|") -> ResultBlock(
            stringResource(R.string.formula_dc_power),
            stringResource(R.string.solved_power, formatQuantity(outcome.substringAfter("|").toDouble())),
        )
        outcome.startsWith("current|") -> {
            val parts = outcome.split("|")
            ResultBlock(
                formulaLabel(parts[2]),
                stringResource(R.string.solved_current, formatQuantity(parts[1].toDouble())),
            )
        }
        outcome == "invalid" -> Text(stringResource(R.string.err_invalid_number), color = MaterialTheme.colorScheme.error)
        outcome == "bad" -> Text(stringResource(R.string.err_inputs), color = MaterialTheme.colorScheme.error)
    }
}

@Composable
fun EnergyScreen(onBack: () -> Unit) {
    var power by rememberSaveable { mutableStateOf("") }
    var hours by rememberSaveable { mutableStateOf("") }
    var tariff by rememberSaveable { mutableStateOf("") }
    var outcome by rememberSaveable { mutableStateOf("") }
    ToolScaffold(stringResource(R.string.open_energy), onBack) { padding ->
        ScrollColumn(padding) {
            Text(stringResource(R.string.energy_help))
            NumberField(stringResource(R.string.power), power) { power = it }
            NumberField(stringResource(R.string.hours), hours) { hours = it }
            NumberField(stringResource(R.string.tariff), tariff) { tariff = it }
            PrimaryButton(stringResource(R.string.calculate)) {
                val watts = parseNumber(power)
                val duration = parseNumber(hours)
                val price = parseNumber(tariff)
                val bill = if (watts != null && duration != null && price != null) {
                    EnergyCost.bill(watts, duration, price)
                } else {
                    null
                }
                outcome = if (power.isBlank() || hours.isBlank() || tariff.isBlank() || watts == null || duration == null || price == null) {
                    "invalid"
                } else if (bill == null) {
                    "bad"
                } else {
                    "${bill.kilowattHours}|${bill.cost}"
                }
            }
            if (outcome == "invalid" || outcome == "bad") {
                Text(
                    stringResource(if (outcome == "invalid") R.string.err_invalid_number else R.string.err_inputs),
                    color = MaterialTheme.colorScheme.error,
                )
            } else if (outcome.contains("|")) {
                val parts = outcome.split("|")
                ResultBlock(
                    stringResource(R.string.formula_energy),
                    stringResource(R.string.energy_result, formatQuantity(parts[0].toDouble())) +
                        "\n" +
                        stringResource(R.string.cost_result, formatQuantity(parts[1].toDouble())),
                )
            }
        }
    }
}

@Composable
private fun formulaLabel(name: String): String {
    val id = runCatching { FormulaId.valueOf(name) }.getOrNull()
    return stringResource(
        when (id) {
            FormulaId.OHM_VOLTAGE -> R.string.formula_ohm_voltage
            FormulaId.OHM_CURRENT -> R.string.formula_ohm_current
            FormulaId.OHM_RESISTANCE -> R.string.formula_ohm_resistance
            FormulaId.OHM_CHECK -> R.string.formula_ohm_check
            FormulaId.DC_POWER -> R.string.formula_dc_power
            FormulaId.CURRENT_SINGLE_PHASE -> R.string.formula_single
            FormulaId.CURRENT_THREE_PHASE -> R.string.formula_three
            FormulaId.ENERGY -> R.string.formula_energy
            null -> R.string.result
        },
    )
}

private fun parseOptional(vararg texts: String): List<Double?>? {
    val values = mutableListOf<Double?>()
    for (text in texts) {
        if (text.isBlank()) {
            values += null
        } else {
            values += parseNumber(text) ?: return null
        }
    }
    return values
}
