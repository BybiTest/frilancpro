package com.example.model

import java.util.UUID

enum class ProjectStatus(val titleFa: String, val colorHex: Long) {
    PROPOSAL_SENT("پیشنهاد ارسال‌شده", 0xFF3B82F6),
    NEGOTIATING("در حال مذاکره", 0xFFF59E0B),
    IN_PROGRESS("در حال انجام", 0xFF10B981),
    COMPLETED("تکمیل‌شده / تسویه", 0xFF6366F1),
    ARCHIVED("بایگانی", 0xFF64748B)
}

data class ClientProject(
    val id: String = UUID.randomUUID().toString(),
    val clientName: String,
    val projectTitle: String,
    val platform: String = "کارلنسر / پونیشا / مستقیم",
    val budgetToman: Long,
    val deadlineDate: String,
    val status: ProjectStatus = ProjectStatus.IN_PROGRESS,
    val nextFollowUp: String = "",
    val notes: String = "",
    val progressPercent: Int = 0
)

val defaultProjects = listOf(
    ClientProject(
        clientName = "شرکت آرتاتک (مهندس مرادی)",
        projectTitle = "طراحی مجدد اپلیکیشن فروشگاهی",
        budgetToman = 24_000_000L,
        deadlineDate = "۲۵ آبان",
        status = ProjectStatus.IN_PROGRESS,
        nextFollowUp = "ارسال پروتوتایپ فیگما",
        progressPercent = 60
    ),
    ClientProject(
        clientName = "آکادمی آموزش آنلاین رایا",
        projectTitle = "طراحی لندینگ پیج جشنواره پاییزه",
        budgetToman = 12_000_000L,
        deadlineDate = "۳۰ آبان",
        status = ProjectStatus.NEGOTIATING,
        nextFollowUp = "تماس تلفنی برای نهایی کردن قرارداد",
        progressPercent = 20
    ),
    ClientProject(
        clientName = "صرافی بین‌المللی نووا",
        projectTitle = "مشاوره تجربه کاربری پنل ترید",
        budgetToman = 45_000_000L,
        deadlineDate = "۱۵ آذر",
        status = ProjectStatus.PROPOSAL_SENT,
        nextFollowUp = "پیگیری ایمیل پروپوزال تا ۲ روز دیگر",
        progressPercent = 10
    )
)
