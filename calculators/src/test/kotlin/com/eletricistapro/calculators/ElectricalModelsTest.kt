package com.eletricistapro.calculators

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ElectricalModelsTest {
    @Test
    fun resistanceFromVoltageAndCurrent() {
        val solved = OhmsLaw.solve(12.0, 0.5, null) as OhmResult.Solved
        assertEquals(24.0, solved.resistanceOhms, 1e-9)
        assertEquals(FormulaId.OHM_RESISTANCE, solved.formula)
    }

    @Test
    fun voltageFromResistanceAndCurrent() {
        val solved = OhmsLaw.solve(null, 2.0, 10.0) as OhmResult.Solved
        assertEquals(20.0, solved.voltageVolts, 1e-9)
    }

    @Test
    fun currentFromVoltageAndResistance() {
        val solved = OhmsLaw.solve(12.0, null, 4.0) as OhmResult.Solved
        assertEquals(3.0, solved.currentAmperes, 1e-9)
    }

    @Test
    fun rejectsInconsistentTriple() {
        val result = OhmsLaw.solve(10.0, 2.0, 10.0)
        assertTrue(result is OhmResult.Invalid)
        assertEquals(OhmError.INCONSISTENT, (result as OhmResult.Invalid).error)
    }

    @Test
    fun acceptsConsistentTriple() {
        val solved = OhmsLaw.solve(10.0, 2.0, 5.0) as OhmResult.Solved
        assertEquals(FormulaId.OHM_CHECK, solved.formula)
    }

    @Test
    fun rejectsZeroResistanceWithVoltage() {
        assertEquals(
            OhmError.ZERO_RESISTANCE,
            (OhmsLaw.solve(10.0, null, 0.0) as OhmResult.Invalid).error,
        )
    }

    @Test
    fun directCurrentPower() {
        val power = DcPower.watts(220.0, 5.0) as PowerResult.Ok
        assertEquals(1100.0, power.watts, 1e-9)
    }

    @Test
    fun singlePhaseCurrentAtUnityPowerFactor() {
        val estimate = CurrentEstimate.amperes(
            activePowerWatts = 1100.0,
            voltageVolts = 220.0,
            powerFactor = 1.0,
            system = SupplySystem.SINGLE_PHASE,
        ) as EstimateResult.Ok
        assertEquals(5.0, estimate.amperes, 1e-9)
    }

    @Test
    fun threePhaseLineCurrentReference() {
        val estimate = CurrentEstimate.amperes(
            activePowerWatts = 3000.0,
            voltageVolts = 380.0,
            powerFactor = 0.9,
            system = SupplySystem.THREE_PHASE_LINE_VOLTAGE,
        ) as EstimateResult.Ok
        assertEquals(5.064, estimate.amperes, 0.001)
    }

    @Test
    fun rejectsPowerFactorAboveOne() {
        val estimate = CurrentEstimate.amperes(1000.0, 220.0, 1.1, SupplySystem.SINGLE_PHASE)
        assertTrue(estimate is EstimateResult.Invalid)
    }

    @Test
    fun energyAndCost() {
        val bill = EnergyCost.bill(powerWatts = 1000.0, hours = 2.0, pricePerKilowattHour = 0.90)
        requireNotNull(bill)
        assertEquals(2.0, bill.kilowattHours, 1e-9)
        assertEquals(1.80, bill.cost, 1e-9)
    }
}
