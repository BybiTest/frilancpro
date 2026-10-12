package com.example.model

import androidx.compose.ui.graphics.Color

enum class AppLanguage(val code: String, val titleFa: String, val titleEn: String) {
    PERSIAN("fa", "فارسی", "Persian"),
    ENGLISH("en", "English", "انگلیسی")
}

enum class FontScale(val scale: Float, val titleFa: String, val titleEn: String) {
    SMALL(0.85f, "کوچک", "Small"),
    MEDIUM(1.0f, "متوسط (استاندارد)", "Medium"),
    LARGE(1.15f, "بزرگ", "Large"),
    EXTRA_LARGE(1.30f, "خیلی بزرگ", "Extra Large")
}

enum class ThemeColorChoice(
    val titleFa: String,
    val titleEn: String,
    val primaryColor: Color,
    val secondaryColor: Color
) {
    ROYAL_GOLD(
        "طلایی سلطنتی",
        "Royal Gold",
        Color(0xFFFFB703),
        Color(0xFFE09A00)
    ),
    EMERALD_GREEN(
        "سبز زمردی",
        "Emerald Green",
        Color(0xFF06D6A0),
        Color(0xFF05B586)
    ),
    CYBER_CYAN(
        "فیروزه‌ای سایبر",
        "Cyber Cyan",
        Color(0xFF00B4D8),
        Color(0xFF0096B4)
    ),
    CRIMSON_RUBY(
        "یاقوتی زرشکی",
        "Crimson Ruby",
        Color(0xFFEF476F),
        Color(0xFFD83058)
    ),
    GALAXY_VIOLET(
        "بنفش کهکشانی",
        "Galaxy Violet",
        Color(0xFF8B5CF6),
        Color(0xFF7C3AED)
    ),
    OCEAN_BLUE(
        "آبی اقیانوسی",
        "Ocean Blue",
        Color(0xFF3B82F6),
        Color(0xFF2563EB)
    ),
    SUNSET_ORANGE(
        "نارنجی آتشین",
        "Sunset Orange",
        Color(0xFFF97316),
        Color(0xFFEA580C)
    )
}

enum class PaymentGateway(val titleFa: String, val titleEn: String, val iconRes: String) {
    CAFE_BAZAAR("کافه بازار (Bazaar)", "Cafe Bazaar", "bazaar"),
    MYKET("مایکت (Myket)", "Myket", "myket")
}

data class AppSettings(
    val language: AppLanguage = AppLanguage.PERSIAN,
    val fontScale: FontScale = FontScale.MEDIUM,
    val themeColor: ThemeColorChoice = ThemeColorChoice.ROYAL_GOLD,
    val isDarkMode: Boolean = true,
    val paymentGateway: PaymentGateway = PaymentGateway.CAFE_BAZAAR
)
