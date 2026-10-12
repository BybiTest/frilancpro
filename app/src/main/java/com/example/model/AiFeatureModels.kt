package com.example.model

data class AtsScanResult(
    val score: Int,
    val summary: String,
    val matchedKeywords: List<String>,
    val missingKeywords: List<String>,
    val recommendations: List<String>,
    val formattingGrade: String = "عالی (A)"
)

data class GeneratedProposal(
    val title: String,
    val tone: String,
    val content: String,
    val estimatedDays: Int,
    val proposedPriceText: String,
    val tipsForWinning: List<String>
)

data class InterviewScenario(
    val id: String,
    val category: String,
    val question: String,
    val intentBehindQuestion: String,
    val winningPoints: List<String>,
    val sampleIdealAnswer: String
)

data class RateCalculatorInput(
    val monthlyLivingExpenses: Long = 25_000_000L, // Toman
    val businessExpenses: Long = 5_000_000L, // Tools, internet, hardware depreciation
    val desiredSavingsMarginPercent: Int = 25, // 25% profit/savings
    val billableHoursPerWeek: Int = 25, // Actual working client hours (not admin)
    val vacationWeeksPerYear: Int = 4
)

data class RateCalculationOutput(
    val hourlyRateToman: Long,
    val dailyRateToman: Long,
    val minimumProjectRateToman: Long,
    val totalAnnualTargetToman: Long,
    val adviceText: String
)
