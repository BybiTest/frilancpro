package com.example.model

enum class SubscriptionTier(val titleFa: String, val badgeColor: Long) {
    FREE("طرح پایه (رایگان)", 0xFF64748B),
    VIP_PRO("اشتراک حرفه‌ای VIP", 0xFFFFB703)
}

data class UserProfile(
    val username: String = "",
    val displayName: String = "",
    val email: String = "",
    val tier: SubscriptionTier = SubscriptionTier.FREE,
    val aiCredits: Int = 5,
    val publicPortfolioSlug: String = "",
    val isPortfolioPublic: Boolean = false,
    val portfolioViewsCount: Int = 0,
    val clientContactCount: Int = 0
)
