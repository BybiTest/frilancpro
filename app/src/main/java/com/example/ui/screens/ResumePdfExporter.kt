package com.example.ui.screens

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.pdf.PdfDocument
import android.text.Layout
import android.text.StaticLayout
import android.text.TextPaint
import com.example.model.ResumeData
import java.io.OutputStream

/**
 * Creates a readable, multi-page PDF resume using Android's native PDF renderer.
 * StaticLayout handles paragraph layout and Android text shaping for Persian/Arabic.
 */
object ResumePdfExporter {
    fun write(context: Context, resume: ResumeData, output: OutputStream) {
        val document = PdfDocument()
        val pageWidth = 595
        val pageHeight = 842
        val margin = 42f
        val contentWidth = (pageWidth - margin * 2).toInt()
        val bodyPaint = TextPaint(Paint.ANTI_ALIAS_FLAG).apply {
            color = android.graphics.Color.rgb(45, 55, 72)
            textSize = 11f
        }
        val titlePaint = TextPaint(Paint.ANTI_ALIAS_FLAG).apply {
            color = android.graphics.Color.rgb(15, 45, 75)
            textSize = 22f
            isFakeBoldText = true
        }
        val headingPaint = TextPaint(Paint.ANTI_ALIAS_FLAG).apply {
            color = android.graphics.Color.rgb(2, 132, 199)
            textSize = 14f
            isFakeBoldText = true
        }
        val pages = mutableListOf<List<Pair<String, Int>>>()
        val blocks = mutableListOf<Pair<String, Int>>()
        blocks += resume.fullName to 0
        blocks += resume.jobTitle to 1
        blocks += listOf(resume.email, resume.phone, resume.city, resume.websiteOrGithub)
            .filter { it.isNotBlank() }.joinToString("   |   ") to 2
        blocks += "خلاصه حرفه‌ای" to 1
        blocks += resume.bio to 2
        if (resume.experiences.isNotEmpty()) {
            blocks += "سوابق کاری و پروژه‌ها" to 1
            resume.experiences.forEach { e ->
                blocks += "${e.role} — ${e.companyOrClient}" to 2
                blocks += "${e.startDate} تا ${e.endDate}" to 2
                blocks += e.description to 2
                e.highlights.filter { it.isNotBlank() }.forEach { blocks += "• $it" to 2 }
            }
        }
        if (resume.educationList.isNotEmpty()) {
            blocks += "تحصیلات" to 1
            resume.educationList.forEach { blocks += "${it.degree} — ${it.institution} (${it.year})" to 2 }
        }
        if (resume.skills.isNotEmpty()) {
            blocks += "مهارت‌ها" to 1
            blocks += resume.skills.joinToString("  •  ") { it.name } to 2
        }
        if (resume.languages.isNotEmpty()) {
            blocks += "زبان‌ها" to 1
            blocks += resume.languages.joinToString("  •  ") to 2
        }

        var pageNumber = 1
        var page = document.startPage(PdfDocument.PageInfo.Builder(pageWidth, pageHeight, pageNumber).create())
        var canvas: Canvas = page.canvas
        var y = margin
        fun startNewPage() {
            document.finishPage(page)
            pageNumber++
            page = document.startPage(PdfDocument.PageInfo.Builder(pageWidth, pageHeight, pageNumber).create())
            canvas = page.canvas
            y = margin
        }
        blocks.forEach { (text, type) ->
            if (text.isBlank()) return@forEach
            val paint = when (type) { 0 -> titlePaint; 1 -> headingPaint; else -> bodyPaint }
            val width = contentWidth
            val layout = StaticLayout.Builder.obtain(text, 0, text.length, paint, width)
                .setAlignment(Layout.Alignment.ALIGN_NORMAL)
                .setTextDirection(android.text.TextDirectionHeuristics.RTL)
                .setIncludePad(true)
                .setLineSpacing(2f, 1.15f)
                .build()
            val gap = if (type == 1) 8f else 4f
            if (y + layout.height + gap > pageHeight - margin) startNewPage()
            canvas.save()
            canvas.translate(margin, y)
            layout.draw(canvas)
            canvas.restore()
            y += layout.height + gap
            if (type == 1) {
                val linePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                    color = android.graphics.Color.rgb(220, 230, 238)
                    strokeWidth = 0.8f
                }
                canvas.drawLine(margin, y, pageWidth - margin, y, linePaint)
                y += 7f
            }
        }
        document.finishPage(page)
        document.writeTo(output)
        document.close()
    }
}
