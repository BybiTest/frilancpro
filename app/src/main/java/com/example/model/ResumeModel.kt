package com.example.model

import java.util.UUID

data class ResumeData(
    val id: String = UUID.randomUUID().toString(),
    val title: String = "رزومه فریلنسری عمومی",
    val fullName: String = "علی رضایی",
    val jobTitle: String = "طراح ارشد رابط کاربری (UI/UX) و محصول",
    val email: String = "ali.rezaei@example.com",
    val phone: String = "۰۹۱۲۳۴۵۶۷۸۹",
    val city: String = "تهران / ریموت (دورکار)",
    val websiteOrGithub: String = "https://dribbble.com/alireza",
    val bio: String = "طراح محصول با بیش از ۵ سال تجربه در طراحی سامانه‌های مقیاس‌پذیر و اپلیکیشن‌های موبایل. تخصص در بهینه‌سازی نرخ تبدیل (CRO)، دیزاین سیستم و تحقیقات کاربر.",
    val experiences: List<WorkExperience> = defaultExperiences,
    val educationList: List<EducationItem> = defaultEducation,
    val skills: List<SkillItem> = defaultSkills,
    val languages: List<String> = listOf("فارسی (مادری)", "انگلیسی (پیشرفته C1)"),
    val templateId: String = "modern_clean"
)

data class WorkExperience(
    val id: String = UUID.randomUUID().toString(),
    val role: String,
    val companyOrClient: String,
    val startDate: String,
    val endDate: String,
    val isRemote: Boolean = true,
    val description: String,
    val highlights: List<String> = emptyList()
)

data class EducationItem(
    val id: String = UUID.randomUUID().toString(),
    val degree: String,
    val institution: String,
    val year: String
)

data class SkillItem(
    val name: String,
    val levelPercentage: Int = 85,
    val category: String = "تخصصی"
)

data class PortfolioProject(
    val id: String = UUID.randomUUID().toString(),
    val title: String,
    val category: String,
    val clientName: String,
    val description: String,
    val metrics: String,
    val projectUrl: String,
    val tags: List<String>
)

val defaultExperiences = listOf(
    WorkExperience(
        role = "طراح محصول فریلنسر",
        companyOrClient = "پلتفرم‌های بین‌المللی (Upwork & Dribbble)",
        startDate = "۱۴۰۱",
        endDate = "اکنون",
        description = "طراحی بیش از ۱۲ اپلیکیشن موبایل و داشبورد فین‌تک برای کارفرمایان داخلی و بین‌المللی.",
        highlights = listOf("افزایش ۴۰ درصدی نرخ تکمیل فرم ثبت‌نام کارفرما", "ایجاد دیزاین سیستم کامل با فریمورک فیگما")
    ),
    WorkExperience(
        role = "سرپرست تیم UI/UX",
        companyOrClient = "استارتاپ نوین‌تک",
        startDate = "۱۳۹۹",
        endDate = "۱۴۰۱",
        description = "مدیریت و هدایت تیم ۳ نفره طراحی، پیاده‌سازی متدولوژی Design Sprint و تست کاربردپذیری."
    )
)

val defaultEducation = listOf(
    EducationItem(
        degree = "کارشناسی ارشد مهندسی نرم‌افزار",
        institution = "دانشگاه صنعتی شریف",
        year = "۱۳۹۹"
    ),
    EducationItem(
        degree = "گواهی تخصصی طراحی UX گوگل (Google UX Certificate)",
        institution = "Coursera",
        year = "۱۴۰۱"
    )
)

val defaultSkills = listOf(
    SkillItem("Figma & Design Systems", 95, "طراحی"),
    SkillItem("UX Research & Prototyping", 90, "طراحی"),
    SkillItem("Wireframing & CRO", 85, "طراحی"),
    SkillItem("مذاکره و مدیریت پروژه فریلنس", 88, "مدیریت"),
    SkillItem("HTML/CSS & Tailwind", 75, "کدنویسی"),
    SkillItem("تحلیل داده با Google Analytics", 80, "تحلیل")
)
