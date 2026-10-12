package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.*
import com.example.ui.theme.*

@Composable
fun ResumeBuilderScreen(
    activeResume: ResumeData,
    allResumes: List<ResumeData>,
    onUpdateResume: (ResumeData) -> Unit,
    onSelectResume: (ResumeData) -> Unit,
    onCreateResume: (String) -> Unit,
    onRequestAiAnalysis: () -> Unit
) {
    val colors = LocalAppColors.current
    val fontScale = LocalFontScale.current.scale

    var isPreviewMode by remember { mutableStateOf(false) }
    var showNewResumeDialog by remember { mutableStateOf(false) }
    var newResumeTitle by remember { mutableStateOf("") }

    // State for inputs
    var fullName by remember(activeResume.id) { mutableStateOf(activeResume.fullName) }
    var jobTitle by remember(activeResume.id) { mutableStateOf(activeResume.jobTitle) }
    var email by remember(activeResume.id) { mutableStateOf(activeResume.email) }
    var phone by remember(activeResume.id) { mutableStateOf(activeResume.phone) }
    var city by remember(activeResume.id) { mutableStateOf(activeResume.city) }
    var bio by remember(activeResume.id) { mutableStateOf(activeResume.bio) }

    fun saveChanges() {
        onUpdateResume(
            activeResume.copy(
                fullName = fullName,
                jobTitle = jobTitle,
                email = email,
                phone = phone,
                city = city,
                bio = bio
            )
        )
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(top = 12.dp, bottom = 96.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Resume Switcher & Action Header
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, colors.cardBorder, RoundedCornerShape(18.dp)),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = colors.cardBackground)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "مدیریت رزومه‌های من",
                            color = colors.textPrimary,
                            fontSize = (15 * fontScale).sp,
                            fontWeight = FontWeight.Bold
                        )
                        IconButton(
                            onClick = { showNewResumeDialog = true },
                            modifier = Modifier.testTag("add_new_resume_btn")
                        ) {
                            Icon(
                                imageVector = Icons.Default.AddCircle,
                                contentDescription = "ایجاد رزومه جدید",
                                tint = colors.primary
                            )
                        }
                    }

                    // Resume chips
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        allResumes.forEach { res ->
                            val isSelected = res.id == activeResume.id
                            FilterChip(
                                selected = isSelected,
                                onClick = { onSelectResume(res) },
                                label = { Text(res.title, fontSize = (11 * fontScale).sp) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = colors.primary,
                                    selectedLabelColor = Navy900,
                                    containerColor = colors.surfaceVariant,
                                    labelColor = colors.textSecondary
                                )
                            )
                        }
                    }
                }
            }
        }

        // Mode Switch (Edit vs Live Preview)
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(colors.surface)
                    .border(1.dp, colors.cardBorder, RoundedCornerShape(12.dp))
                    .padding(4.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Button(
                    onClick = { isPreviewMode = false },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (!isPreviewMode) colors.primary else Color.Transparent,
                        contentColor = if (!isPreviewMode) Navy900 else colors.textSecondary
                    )
                ) {
                    Icon(Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("ویرایش سریع", fontSize = (13 * fontScale).sp, fontWeight = FontWeight.Bold)
                }

                Button(
                    onClick = {
                        saveChanges()
                        isPreviewMode = true
                    },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isPreviewMode) colors.primary else Color.Transparent,
                        contentColor = if (isPreviewMode) Navy900 else colors.textSecondary
                    )
                ) {
                    Icon(Icons.Default.Visibility, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("پیش‌نمایش زنده", fontSize = (13 * fontScale).sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        if (!isPreviewMode) {
            // Edit Mode Form
            item {
                Text(
                    text = "اطلاعات هویتی و تخصصی فریلنسر",
                    color = colors.primary,
                    fontSize = (14 * fontScale).sp,
                    fontWeight = FontWeight.Bold
                )
            }

            item {
                OutlinedTextField(
                    value = fullName,
                    onValueChange = { fullName = it },
                    label = { Text("نام و نام خانوادگی") },
                    modifier = Modifier.fillMaxWidth().testTag("resume_fullname_input"),
                    shape = RoundedCornerShape(12.dp),
                    colors = appFieldColors(colors)
                )
            }

            item {
                OutlinedTextField(
                    value = jobTitle,
                    onValueChange = { jobTitle = it },
                    label = { Text("عنوان تخصصی شغلی (مثلاً طراح ارشد UI/UX)") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = appFieldColors(colors)
                )
            }

            item {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = phone,
                        onValueChange = { phone = it },
                        label = { Text("تلفن / واتساپ") },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp),
                        colors = appFieldColors(colors)
                    )
                    OutlinedTextField(
                        value = city,
                        onValueChange = { city = it },
                        label = { Text("شهر / ریموت") },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp),
                        colors = appFieldColors(colors)
                    )
                }
            }

            item {
                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("ایمیل کاری") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = appFieldColors(colors)
                )
            }

            // About Me with AI improve suggestion
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, colors.cardBorder, RoundedCornerShape(14.dp)),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = colors.cardBackground)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("درباره من (خلاصه تجربیات)", color = colors.textPrimary, fontSize = (13 * fontScale).sp, fontWeight = FontWeight.Bold)
                            TextButton(
                                onClick = {
                                    bio = "طراح محصول با سابقه طراحی و لانچ بیش از ۱۵ محصول دیجیتال مقیاس‌پذیر. متعهد به تحویل دقیق، تعامل شفاف و افزایش سودآوری مشتریان."
                                }
                            ) {
                                Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = colors.primary, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("پیشنهاد هوش مصنوعی", color = colors.primary, fontSize = (11 * fontScale).sp)
                            }
                        }

                        OutlinedTextField(
                            value = bio,
                            onValueChange = { bio = it },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(10.dp),
                            minLines = 3,
                            colors = appFieldColors(colors)
                        )
                    }
                }
            }

            // Experiences List Card
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, colors.cardBorder, RoundedCornerShape(16.dp)),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = colors.cardBackground)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "سوابق پروژه‌ها و همکاری‌ها (${activeResume.experiences.size})",
                            color = colors.textPrimary,
                            fontSize = (14 * fontScale).sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        activeResume.experiences.forEach { exp ->
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 6.dp)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(colors.surfaceVariant)
                                    .padding(10.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(exp.role, color = colors.primary, fontWeight = FontWeight.Bold, fontSize = (13 * fontScale).sp)
                                    Text("${exp.startDate} - ${exp.endDate}", color = colors.textSecondary, fontSize = (10 * fontScale).sp)
                                }
                                Text(exp.companyOrClient, color = colors.textPrimary, fontSize = (12 * fontScale).sp)
                                Text(exp.description, color = colors.textSecondary, fontSize = (11 * fontScale).sp, modifier = Modifier.padding(top = 4.dp))
                            }
                        }
                    }
                }
            }

            // Skills Card
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, colors.cardBorder, RoundedCornerShape(16.dp)),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = colors.cardBackground)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "مهارت‌های فنی و نرم‌افزاری",
                            color = colors.textPrimary,
                            fontSize = (14 * fontScale).sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            activeResume.skills.take(4).forEach { skill ->
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(colors.surfaceVariant)
                                        .padding(horizontal = 8.dp, vertical = 5.dp)
                                ) {
                                    Text(skill.name, color = colors.primary, fontSize = (11 * fontScale).sp, fontWeight = FontWeight.SemiBold)
                                }
                            }
                        }
                    }
                }
            }

            // Save and AI ATS analyze action
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Button(
                        onClick = { saveChanges() },
                        modifier = Modifier.weight(1f).height(48.dp).testTag("save_resume_button"),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = colors.primary, contentColor = Navy900)
                    ) {
                        Icon(Icons.Default.Save, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("ذخیره تغییرات", fontWeight = FontWeight.Bold, fontSize = (13 * fontScale).sp)
                    }

                    OutlinedButton(
                        onClick = {
                            saveChanges()
                            onRequestAiAnalysis()
                        },
                        modifier = Modifier.weight(1f).height(48.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = colors.primary),
                        border = ButtonDefaults.outlinedButtonBorder(enabled = true).copy(brush = Brush.horizontalGradient(listOf(colors.primary, colors.secondary)))
                    ) {
                        Icon(Icons.Default.Analytics, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("تحلیل نمره ATS", fontSize = (12 * fontScale).sp, fontWeight = FontWeight.SemiBold)
                    }
                }
            }
        } else {
            // Live Preview of the Resume
            item {
                ResumeSheetPreview(resume = activeResume)
            }
        }
    }

    // Dialog for creating a new resume
    if (showNewResumeDialog) {
        AlertDialog(
            onDismissRequest = { showNewResumeDialog = false },
            title = { Text("ساخت نسخه جدید رزومه", color = colors.textPrimary) },
            text = {
                OutlinedTextField(
                    value = newResumeTitle,
                    onValueChange = { newResumeTitle = it },
                    label = { Text("عنوان رزومه (مثلاً رزومه پونیشا یا خارجی)") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = appFieldColors(colors)
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (newResumeTitle.isNotBlank()) {
                            onCreateResume(newResumeTitle)
                            newResumeTitle = ""
                            showNewResumeDialog = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = colors.primary, contentColor = Navy900)
                ) {
                    Text("ایجاد")
                }
            },
            dismissButton = {
                TextButton(onClick = { showNewResumeDialog = false }) {
                    Text("انصراف", color = colors.textSecondary)
                }
            },
            containerColor = colors.surface
        )
    }
}

@Composable
fun ResumeSheetPreview(resume: ResumeData) {
    val colors = LocalAppColors.current

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.5.dp, colors.primary.copy(alpha = 0.5f), RoundedCornerShape(16.dp)),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = if (colors.isDark) Color(0xFFFAFAFA) else Color.White)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(resume.fullName, color = Color(0xFF0F172A), fontSize = 20.sp, fontWeight = FontWeight.Bold)
                    Text(resume.jobTitle, color = Color(0xFF0284C7), fontSize = 13.sp, fontWeight = FontWeight.Medium)
                }
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(0xFFE2E8F0))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text("استاندارد ATS", color = Color(0xFF475569), fontSize = 10.sp, fontWeight = FontWeight.Bold)
                }
            }

            HorizontalDivider(color = Color(0xFFCBD5E1), modifier = Modifier.padding(vertical = 12.dp))

            // Contact Info Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(resume.email, color = Color(0xFF475569), fontSize = 11.sp)
                Text(resume.phone, color = Color(0xFF475569), fontSize = 11.sp)
                Text(resume.city, color = Color(0xFF475569), fontSize = 11.sp)
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Bio
            Text("خلاصه حرفه‌ای", color = Color(0xFF0F172A), fontSize = 13.sp, fontWeight = FontWeight.Bold)
            Text(
                resume.bio,
                color = Color(0xFF334155),
                fontSize = 12.sp,
                lineHeight = 18.sp,
                modifier = Modifier.padding(top = 4.dp, bottom = 12.dp)
            )

            // Experience
            Text("سوابق کاری و پروژه‌ها", color = Color(0xFF0F172A), fontSize = 13.sp, fontWeight = FontWeight.Bold)
            resume.experiences.forEach { exp ->
                Column(modifier = Modifier.padding(top = 6.dp, bottom = 8.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(exp.role, color = Color(0xFF0F172A), fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                        Text("${exp.startDate} - ${exp.endDate}", color = Color(0xFF64748B), fontSize = 10.sp)
                    }
                    Text(exp.companyOrClient, color = Color(0xFF0284C7), fontSize = 11.sp)
                    Text(exp.description, color = Color(0xFF475569), fontSize = 11.sp)
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Skills
            Text("مهارت‌های کلیدی", color = Color(0xFF0F172A), fontSize = 13.sp, fontWeight = FontWeight.Bold)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 6.dp),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                resume.skills.take(5).forEach { s ->
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(Color(0xFFE0F2FE))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(s.name, color = Color(0xFF0369A1), fontSize = 10.sp, fontWeight = FontWeight.SemiBold)
                    }
                }
            }
        }
    }
}

@Composable
fun appFieldColors(colors: AppColors) = OutlinedTextFieldDefaults.colors(
    focusedBorderColor = colors.primary,
    unfocusedBorderColor = colors.cardBorder,
    focusedLabelColor = colors.primary,
    unfocusedLabelColor = colors.textSecondary,
    focusedTextColor = colors.textPrimary,
    unfocusedTextColor = colors.textPrimary,
    focusedContainerColor = colors.inputBackground,
    unfocusedContainerColor = colors.inputBackground
)
