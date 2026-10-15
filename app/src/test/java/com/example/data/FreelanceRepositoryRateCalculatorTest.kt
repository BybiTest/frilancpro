package com.example.data

import com.example.model.RateCalculatorInput
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class FreelanceRepositoryRateCalculatorTest {
    private val repository = FreelanceRepository()

    @Test
    fun defaultInputsProduceExpectedTomanRates() {
        val result = repository.calculateFreelanceRate(RateCalculatorInput())

        assertEquals(375_000L, result.hourlyRateToman)
        assertEquals(2_250_000L, result.dailyRateToman)
        assertEquals(5_625_000L, result.minimumProjectRateToman)
        assertEquals(450_000_000L, result.totalAnnualTargetToman)
    }

    @Test
    fun negativeInputsNeverProduceNegativeRates() {
        val result = repository.calculateFreelanceRate(
            RateCalculatorInput(
                monthlyLivingExpenses = -10_000L,
                businessExpenses = -20_000L,
                desiredSavingsMarginPercent = -100,
                billableHoursPerWeek = 0,
                vacationWeeksPerYear = -5
            )
        )

        assertEquals(0L, result.hourlyRateToman)
        assertEquals(0L, result.dailyRateToman)
        assertEquals(0L, result.minimumProjectRateToman)
        assertEquals(0L, result.totalAnnualTargetToman)
    }

    @Test
    fun extremeInputsSaturateInsteadOfOverflowing() {
        val result = repository.calculateFreelanceRate(
            RateCalculatorInput(
                monthlyLivingExpenses = Long.MAX_VALUE,
                businessExpenses = Long.MAX_VALUE,
                desiredSavingsMarginPercent = Int.MAX_VALUE,
                billableHoursPerWeek = Int.MAX_VALUE,
                vacationWeeksPerYear = Int.MIN_VALUE
            )
        )

        assertTrue(result.hourlyRateToman >= 0L)
        assertTrue(result.dailyRateToman >= 0L)
        assertTrue(result.minimumProjectRateToman >= 0L)
        assertEquals(Long.MAX_VALUE, result.totalAnnualTargetToman)
    }

    @Test
    fun newRepositoryDoesNotShowFabricatedClientProjects() {
        assertTrue(repository.clientProjects.value.isEmpty())
    }
}
