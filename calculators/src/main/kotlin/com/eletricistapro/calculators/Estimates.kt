package com.eletricistapro.calculators

/**
 * Potência em corrente contínua, P = U × I.
 * Não distingue potência ativa, reativa e aparente.
 */
object DcPower {
    fun watts(voltageVolts: Double, currentAmperes: Double): PowerResult {
        if (!voltageVolts.isFinite() || !currentAmperes.isFinite() || voltageVolts < 0.0 || currentAmperes < 0.0) {
            return PowerResult.Invalid
        }
        return PowerResult.Ok(voltageVolts * currentAmperes, FormulaId.DC_POWER)
    }
}

sealed class PowerResult {
    data class Ok(val watts: Double, val formula: FormulaId) : PowerResult()
    data object Invalid : PowerResult()
}

enum class SupplySystem {
    /** Tensão entre fase e neutro, ou entre as duas fases de um circuito monofásico. */
    SINGLE_PHASE,

    /** Tensão de linha entre fases. I = P / (√3 × U × FP). */
    THREE_PHASE_LINE_VOLTAGE,
}

/**
 * Corrente estimada a partir da potência ativa.
 *
 * Monofásico: I = P / (U × FP).
 * Trifásico, com tensão de linha: I = P / (√3 × U × FP).
 *
 * O fator de potência entra porque a corrente depende da potência aparente.
 * O resultado não escolhe seção de condutor nem disjuntor: faltam método de
 * instalação, temperatura, agrupamento, queda de tensão e curva de disparo.
 */
object CurrentEstimate {
    fun amperes(
        activePowerWatts: Double,
        voltageVolts: Double,
        powerFactor: Double,
        system: SupplySystem,
    ): EstimateResult {
        if (!activePowerWatts.isFinite() || activePowerWatts < 0.0) return EstimateResult.Invalid
        if (!voltageVolts.isFinite() || voltageVolts <= 0.0) return EstimateResult.Invalid
        if (!powerFactor.isFinite() || powerFactor <= 0.0 || powerFactor > 1.0) return EstimateResult.Invalid
        val divisor = when (system) {
            SupplySystem.SINGLE_PHASE -> voltageVolts * powerFactor
            SupplySystem.THREE_PHASE_LINE_VOLTAGE -> kotlin.math.sqrt(3.0) * voltageVolts * powerFactor
        }
        val formula = when (system) {
            SupplySystem.SINGLE_PHASE -> FormulaId.CURRENT_SINGLE_PHASE
            SupplySystem.THREE_PHASE_LINE_VOLTAGE -> FormulaId.CURRENT_THREE_PHASE
        }
        return EstimateResult.Ok(activePowerWatts / divisor, formula)
    }
}

sealed class EstimateResult {
    data class Ok(val amperes: Double, val formula: FormulaId) : EstimateResult()
    data object Invalid : EstimateResult()
}

/**
 * Energia em kWh e custo linear. A tarifa é usada na unidade informada;
 * não há conversão de moeda.
 */
object EnergyCost {
    fun bill(powerWatts: Double, hours: Double, pricePerKilowattHour: Double): EnergyBill? {
        if (!powerWatts.isFinite() || !hours.isFinite() || !pricePerKilowattHour.isFinite()) return null
        if (powerWatts < 0.0 || hours < 0.0 || pricePerKilowattHour < 0.0) return null
        val kilowattHours = powerWatts * hours / 1000.0
        return EnergyBill(kilowattHours, kilowattHours * pricePerKilowattHour)
    }
}

data class EnergyBill(
    val kilowattHours: Double,
    val cost: Double,
)
