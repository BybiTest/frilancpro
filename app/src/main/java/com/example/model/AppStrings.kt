package com.example.model

object AppStrings {
    fun appTitle(lang: AppLanguage) = if (lang == AppLanguage.PERSIAN) "فریلنس‌پرو" else "FreelancePro"
    fun appSubtitle(lang: AppLanguage) = if (lang == AppLanguage.PERSIAN) "دستیار شغلی و رزومه‌ساز هوشمند" else "AI Career Assistant & CV Builder"

    // Tabs
    fun tabResume(lang: AppLanguage) = if (lang == AppLanguage.PERSIAN) "رزومه" else "Resume"
    fun tabPortfolio(lang: AppLanguage) = if (lang == AppLanguage.PERSIAN) "پورتفولیو" else "Portfolio"
    fun tabAi(lang: AppLanguage) = if (lang == AppLanguage.PERSIAN) "دستیار AI" else "AI Assistant"
    fun tabRate(lang: AppLanguage) = if (lang == AppLanguage.PERSIAN) "نرخ‌گذاری" else "Pricing"
    fun tabCrm(lang: AppLanguage) = if (lang == AppLanguage.PERSIAN) "پروژه‌ها" else "Projects"
    fun tabSettings(lang: AppLanguage) = if (lang == AppLanguage.PERSIAN) "تنظیمات" else "Settings"

    // Settings
    fun settingsTitle(lang: AppLanguage) = if (lang == AppLanguage.PERSIAN) "منوی تنظیمات و شخصی‌سازی" else "Settings & Customization"
    fun languageSection(lang: AppLanguage) = if (lang == AppLanguage.PERSIAN) "زبان برنامه (Language)" else "App Language"
    fun fontSizeSection(lang: AppLanguage) = if (lang == AppLanguage.PERSIAN) "اندازه قلم و متون" else "Text Font Size"
    fun themeColorSection(lang: AppLanguage) = if (lang == AppLanguage.PERSIAN) "پالت رنگی برنامه (۷ رنگ جذاب)" else "App Theme Colors (7 Accents)"
    fun darkModeSection(lang: AppLanguage) = if (lang == AppLanguage.PERSIAN) "حالت شب / روز (Dark / Light)" else "Dark / Light Mode"
    fun adTestSection(lang: AppLanguage) = if (lang == AppLanguage.PERSIAN) "تست سیستم تبلیغات تپسی / تپسل" else "Tapsell / Tapsi Ads Engine"
    fun storeBillingSection(lang: AppLanguage) = if (lang == AppLanguage.PERSIAN) "درگاه خرید اشتراک (کافه بازار / مایکت)" else "Bazaar / Myket In-App Purchase"
    fun aboutSection(lang: AppLanguage) = if (lang == AppLanguage.PERSIAN) "درباره برنامه و سازنده" else "About App & Developer"

    // About Details
    fun developerName(lang: AppLanguage) = if (lang == AppLanguage.PERSIAN) "سیدحمیدموسوی زاده" else "Seyed Hamid Mousavizadeh"
    fun appVersion(lang: AppLanguage) = if (lang == AppLanguage.PERSIAN) "نسخه ۱.۰.۰ پایدار (PRO Edition)" else "Version 1.0.0 Stable (PRO Edition)"
    fun appDescription(lang: AppLanguage) = if (lang == AppLanguage.PERSIAN)
        "پلتفرم پیشرفته فریلنس‌پرو برای توانمندسازی فریلنسرها، ساخت آسان رزومه استاندارد ATS، پورتفولیوی آنلاین و مذاکره حرفه‌ای قیمت."
    else
        "Advanced FreelancePro platform to empower freelancers with fast ATS resume building, public showcase portfolio, and smart rate calculation."
}
