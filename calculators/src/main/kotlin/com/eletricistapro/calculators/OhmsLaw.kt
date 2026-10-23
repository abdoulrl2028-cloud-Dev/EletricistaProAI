package com.eletricistapro.calculators

/**
 * Lei de Ohm para resistência constante em corrente contínua: U = R × I.
 *
 * Não modela impedância, corrente alternada, queda de tensão em condutor
 * nem escolha de cabo ou disjuntor.
 */
object OhmsLaw {
    private const val RELATIVE_TOLERANCE = 1.0e-4

    fun solve(
        voltageVolts: Double?,
        currentAmperes: Double?,
        resistanceOhms: Double?,
    ): OhmResult {
        val provided = listOf(voltageVolts, currentAmperes, resistanceOhms).count { it != null }
        if (provided != 2 && provided != 3) {
            return OhmResult.Invalid(OhmError.NEED_TWO_OR_THREE_VALUES)
        }
        if (listOfNotNull(voltageVolts, currentAmperes, resistanceOhms).any { !it.isFinite() || it < 0.0 }) {
            return OhmResult.Invalid(OhmError.NEGATIVE_OR_NON_FINITE)
        }

        if (provided == 3) {
            val voltage = voltageVolts ?: return OhmResult.Invalid(OhmError.NEED_TWO_OR_THREE_VALUES)
            val current = currentAmperes ?: return OhmResult.Invalid(OhmError.NEED_TWO_OR_THREE_VALUES)
            val resistance = resistanceOhms ?: return OhmResult.Invalid(OhmError.NEED_TWO_OR_THREE_VALUES)
            val expected = resistance * current
            val scale = maxOf(1.0, kotlin.math.abs(voltage), kotlin.math.abs(expected))
            if (kotlin.math.abs(voltage - expected) > RELATIVE_TOLERANCE * scale) {
                return OhmResult.Invalid(OhmError.INCONSISTENT)
            }
            return OhmResult.Solved(voltage, current, resistance, FormulaId.OHM_CHECK)
        }

        if (voltageVolts != null && currentAmperes != null) {
            if (currentAmperes == 0.0) {
                return OhmResult.Invalid(OhmError.ZERO_CURRENT)
            }
            return OhmResult.Solved(
                voltageVolts,
                currentAmperes,
                voltageVolts / currentAmperes,
                FormulaId.OHM_RESISTANCE,
            )
        }
        if (voltageVolts != null && resistanceOhms != null) {
            if (resistanceOhms == 0.0) {
                return OhmResult.Invalid(OhmError.ZERO_RESISTANCE)
            }
            return OhmResult.Solved(
                voltageVolts,
                voltageVolts / resistanceOhms,
                resistanceOhms,
                FormulaId.OHM_CURRENT,
            )
        }
        val current = currentAmperes ?: return OhmResult.Invalid(OhmError.NEED_TWO_OR_THREE_VALUES)
        val resistance = resistanceOhms ?: return OhmResult.Invalid(OhmError.NEED_TWO_OR_THREE_VALUES)
        return OhmResult.Solved(current * resistance, current, resistance, FormulaId.OHM_VOLTAGE)
    }
}

sealed class OhmResult {
    data class Solved(
        val voltageVolts: Double,
        val currentAmperes: Double,
        val resistanceOhms: Double,
        val formula: FormulaId,
    ) : OhmResult()

    data class Invalid(val error: OhmError) : OhmResult()
}

enum class OhmError {
    NEED_TWO_OR_THREE_VALUES,
    NEGATIVE_OR_NON_FINITE,
    ZERO_RESISTANCE,
    ZERO_CURRENT,
    INCONSISTENT,
}

enum class FormulaId {
    OHM_VOLTAGE,
    OHM_CURRENT,
    OHM_RESISTANCE,
    OHM_CHECK,
    DC_POWER,
    CURRENT_SINGLE_PHASE,
    CURRENT_THREE_PHASE,
    ENERGY,
}
