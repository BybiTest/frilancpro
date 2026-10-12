package com.example.model

enum class SubscriptionTier(val titleFa: String, val badgeColor: Long) {
    FREE("طرح پایه (رایگان)", 0xFF64748B),
    VIP_PRO("اشتراک حرفه‌ای VIP", 0xFFFFB703)
}

data class UserProfile(
    val username: String = "alireza_ui",
    val displayName: String = "علی رضایی",
    val email: String = "ali.rezaei@example.com",
    val tier: SubscriptionTier = SubscriptionTier.FREE,
    val aiCredits: Int = 5,
    val publicPortfolioSlug: String = "alireza_ui",
    val isPortfolioPublic: Boolean = true,
    val portfolioViewsCount: Int = 248,
    val clientContactCount: Int = 14
)
