package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ManageSearch
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.*
import com.example.ui.theme.*

enum class AiSubTab(val titleFa: String) {
    ATS_SCANNER("تحلیل نمره ATS"),
    PROPOSAL_WRITER("پیشنهادنویسی پروژه"),
    INTERVIEW_PREP("شبیه‌ساز مصاحبه")
}

@Composable
fun AiAssistantScreen(
    userCredits: Int,
    isVip: Boolean,
    isAtsScanning: Boolean,
    atsResult: AtsScanResult?,
    isGeneratingProposal: Boolean,
    generatedProposal: GeneratedProposal?,
    interviewScenarios: List<InterviewScenario>,
    onRunAtsScan: (String) -> Unit,
    onGenerateProposal: (String, String, String) -> Unit,
    onOpenVipModal: () -> Unit
) {
    val colors = LocalAppColors.current
    val fontScale = LocalFontScale.current.scale
    var selectedSubTab by remember { mutableStateOf(AiSubTab.ATS_SCANNER) }

    // Inputs for ATS
    var jobDescInput by remember {
        mutableStateOf(
            "به یک طراح ارشد UI/UX برای بازطراحی اپلیکیشن فروشگاهی با تسلط کامل به فیگما، دیزاین سیستم و تحقیقات کاربر نیازمندیم. تحویل به صورت پروتوتایپ تست‌شده با سنجه‌های CRO."
        )
    }

    // Inputs for Proposal
    var proposalProjectTitle by remember { mutableStateOf("طراحی مجدد اپلیکیشن فین‌تک پی‌والت") }
    var proposalProjectDetails by remember { mutableStateOf("کارفرما به دنبال کاهش نرخ پرش کاربر و ساده‌سازی تراکنش‌هاست. سابقه کار فین‌تک مزیت محسوب می‌شود.") }
    var selectedTone by remember { mutableStateOf("حرفه‌ای و نتیجه‌گرا") }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(top = 12.dp, bottom = 96.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // AI Credit & VIP status banner
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, colors.cardBorder, RoundedCornerShape(16.dp)),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = colors.cardBackground)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(38.dp)
                                .clip(CircleShape)
                                .background(colors.primary.copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = colors.primary, modifier = Modifier.size(20.dp))
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text("دستیار هوش مصنوعی فریلنسر", color = colors.textPrimary, fontWeight = FontWeight.Bold, fontSize = (13 * fontScale).sp)
                            Text(
                                if (isVip) "اشتراک VIP نامحدود فعال است" else "اعتبار باقی‌مانده: $userCredits بار",
                                color = if (isVip) Emerald500 else colors.textSecondary,
                                fontSize = (11 * fontScale).sp
                            )
                        }
                    }

                    if (!isVip) {
                        Button(
                            onClick = onOpenVipModal,
                            colors = ButtonDefaults.buttonColors(containerColor = colors.primary, contentColor = Navy900),
                            shape = RoundedCornerShape(10.dp),
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Text("افزایش اعتبار", fontSize = (11 * fontScale).sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        // Sub-tabs switcher
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(colors.surface)
                    .border(1.dp, colors.cardBorder, RoundedCornerShape(14.dp))
                    .padding(4.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                AiSubTab.values().forEach { tab ->
                    val isSelected = tab == selectedSubTab
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(10.dp))
                            .background(if (isSelected) colors.primary else Color.Transparent)
                            .clickable { selectedSubTab = tab }
                            .padding(vertical = 8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = tab.titleFa,
                            color = if (isSelected) Navy900 else colors.textSecondary,
                            fontSize = (12 * fontScale).sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                        )
                    }
                }
            }
        }

        when (selectedSubTab) {
            AiSubTab.ATS_SCANNER -> {
                // ATS Scanner section
                item {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(1.dp, colors.cardBorder, RoundedCornerShape(16.dp)),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = colors.cardBackground)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text("بررسی تطابق رزومه با آگهی پروژه (ATS Score)", color = colors.textPrimary, fontSize = (14 * fontScale).sp, fontWeight = FontWeight.Bold)
                            Text(
                                "متن آگهی استخدام یا پروژه کارفرما را وارد کنید تا هوش مصنوعی رزومه شما را اسکن کند.",
                                color = colors.textSecondary,
                                fontSize = (11 * fontScale).sp,
                                modifier = Modifier.padding(top = 2.dp, bottom = 10.dp)
                            )

                            OutlinedTextField(
                                value = jobDescInput,
                                onValueChange = { jobDescInput = it },
                                label = { Text("متن آگهی پروژه کارفرما") },
                                modifier = Modifier.fillMaxWidth().testTag("job_desc_input"),
                                shape = RoundedCornerShape(12.dp),
                                minLines = 3,
                                colors = appFieldColors(colors)
                            )

                            Spacer(modifier = Modifier.height(12.dp))

                            Button(
                                onClick = { onRunAtsScan(jobDescInput) },
                                modifier = Modifier.fillMaxWidth().height(48.dp).testTag("run_ats_scan_btn"),
                                enabled = !isAtsScanning && jobDescInput.isNotBlank(),
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = colors.primary, contentColor = Navy900)
                            ) {
                                if (isAtsScanning) {
                                    CircularProgressIndicator(modifier = Modifier.size(20.dp), color = Navy900, strokeWidth = 2.dp)
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("در حال تحلیل با هوش مصنوعی...", fontWeight = FontWeight.Bold)
                                } else {
                                    Icon(Icons.AutoMirrored.Filled.ManageSearch, contentDescription = null, modifier = Modifier.size(20.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("اسکن و محاسبه نمره ATS", fontWeight = FontWeight.Bold, fontSize = (13 * fontScale).sp)
                                }
                            }
                        }
                    }
                }

                if (atsResult != null) {
                    item {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .border(1.5.dp, colors.primary.copy(alpha = 0.5f), RoundedCornerShape(16.dp)),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = colors.cardBackground)
                        ) {
                            Column(modifier = Modifier.padding(18.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text("نتیجه ارزیابی هوش مصنوعی", color = colors.textPrimary, fontWeight = FontWeight.Bold, fontSize = (14 * fontScale).sp)
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(colors.primary)
                                            .padding(horizontal = 10.dp, vertical = 4.dp)
                                    ) {
                                        Text("${atsResult.score}٪ تطابق", color = Navy900, fontWeight = FontWeight.ExtraBold, fontSize = (13 * fontScale).sp)
                                    }
                                }

                                Text(atsResult.summary, color = colors.textPrimary, fontSize = (12 * fontScale).sp, lineHeight = (18 * fontScale).sp, modifier = Modifier.padding(vertical = 10.dp))

                                // Keywords
                                Text("کلمات کلیدی تطبیق‌یافته:", color = colors.primary, fontWeight = FontWeight.Bold, fontSize = (12 * fontScale).sp)
                                Row(
                                    modifier = Modifier.fillMaxWidth().padding(top = 4.dp, bottom = 8.dp),
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    atsResult.matchedKeywords.forEach { kw ->
                                        Box(
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(6.dp))
                                                .background(colors.surfaceVariant)
                                                .padding(horizontal = 8.dp, vertical = 4.dp)
                                        ) {
                                            Text(kw, color = colors.primary, fontSize = (10 * fontScale).sp, fontWeight = FontWeight.SemiBold)
                                        }
                                    }
                                }

                                Text("کلمات کلیدی پیشنهادی برای افزودن:", color = Coral500, fontWeight = FontWeight.Bold, fontSize = (12 * fontScale).sp)
                                Row(
                                    modifier = Modifier.fillMaxWidth().padding(top = 4.dp, bottom = 10.dp),
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    atsResult.missingKeywords.forEach { kw ->
                                        Box(
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(6.dp))
                                                .background(colors.surfaceVariant)
                                                .padding(horizontal = 8.dp, vertical = 4.dp)
                                        ) {
                                            Text("+ $kw", color = Coral500, fontSize = (10 * fontScale).sp, fontWeight = FontWeight.SemiBold)
                                        }
                                    }
                                }

                                // Recommendations
                                Text("اقدامات پیشنهادی برای برنده شدن:", color = colors.textPrimary, fontWeight = FontWeight.Bold, fontSize = (12 * fontScale).sp)
                                atsResult.recommendations.forEach { rec ->
                                    Row(modifier = Modifier.padding(top = 4.dp), verticalAlignment = Alignment.Top) {
                                        Icon(Icons.Default.CheckCircle, contentDescription = null, tint = colors.primary, modifier = Modifier.size(15.dp))
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(rec, color = colors.textSecondary, fontSize = (11 * fontScale).sp, lineHeight = (16 * fontScale).sp)
                                    }
                                }
                            }
                        }
                    }
                }
            }

            AiSubTab.PROPOSAL_WRITER -> {
                // Proposal generator section
                item {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(1.dp, colors.cardBorder, RoundedCornerShape(16.dp)),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = colors.cardBackground)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text("تولید پیشنهاد و پروپوزال برنده (Proposal Generator)", color = colors.textPrimary, fontSize = (14 * fontScale).sp, fontWeight = FontWeight.Bold)

                            Spacer(modifier = Modifier.height(10.dp))

                            OutlinedTextField(
                                value = proposalProjectTitle,
                                onValueChange = { proposalProjectTitle = it },
                                label = { Text("عنوان آگهی پروژه") },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp),
                                colors = appFieldColors(colors)
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            OutlinedTextField(
                                value = proposalProjectDetails,
                                onValueChange = { proposalProjectDetails = it },
                                label = { Text("توضیحات و نیازمندی‌های کارفرما") },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp),
                                minLines = 2,
                                colors = appFieldColors(colors)
                            )

                            Spacer(modifier = Modifier.height(10.dp))

                            Text("لحن و سبک نگارش:", color = colors.textSecondary, fontSize = (12 * fontScale).sp)
                            Row(
                                modifier = Modifier.fillMaxWidth().padding(top = 4.dp),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                listOf("حرفه‌ای و نتیجه‌گرا", "سریع و مستقیم", "بین‌المللی (انگلیسی)").forEach { tone ->
                                    val isToneSelected = tone == selectedTone
                                    FilterChip(
                                        selected = isToneSelected,
                                        onClick = { selectedTone = tone },
                                        label = { Text(tone, fontSize = (11 * fontScale).sp) },
                                        colors = FilterChipDefaults.filterChipColors(
                                            selectedContainerColor = colors.primary,
                                            selectedLabelColor = Navy900,
                                            containerColor = colors.surfaceVariant,
                                            labelColor = colors.textSecondary
                                        )
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            Button(
                                onClick = {
                                    onGenerateProposal(proposalProjectTitle, proposalProjectDetails, selectedTone)
                                },
                                modifier = Modifier.fillMaxWidth().height(48.dp).testTag("generate_proposal_btn"),
                                enabled = !isGeneratingProposal,
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = colors.primary, contentColor = Navy900)
                            ) {
                                if (isGeneratingProposal) {
                                    CircularProgressIndicator(modifier = Modifier.size(20.dp), color = Navy900, strokeWidth = 2.dp)
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("در حال نگارش با هوش مصنوعی...", fontWeight = FontWeight.Bold)
                                } else {
                                    Icon(Icons.AutoMirrored.Filled.Send, contentDescription = null, modifier = Modifier.size(18.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("تولید پروپوزال اختصاصی", fontWeight = FontWeight.Bold, fontSize = (13 * fontScale).sp)
                                }
                            }
                        }
                    }
                }

                if (generatedProposal != null) {
                    item {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .border(1.dp, colors.cardBorder, RoundedCornerShape(16.dp)),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = colors.cardBackground)
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text(generatedProposal.title, color = colors.primary, fontSize = (14 * fontScale).sp, fontWeight = FontWeight.Bold)
                                Spacer(modifier = Modifier.height(8.dp))

                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(10.dp))
                                        .background(colors.inputBackground)
                                        .border(1.dp, colors.cardBorder, RoundedCornerShape(10.dp))
                                        .padding(12.dp)
                                ) {
                                    Text(
                                        text = generatedProposal.content,
                                        color = colors.textPrimary,
                                        fontSize = (12 * fontScale).sp,
                                        lineHeight = (18 * fontScale).sp
                                    )
                                }

                                Spacer(modifier = Modifier.height(12.dp))

                                Text("نکات طلایی ارسال:", color = colors.primary, fontWeight = FontWeight.Bold, fontSize = (12 * fontScale).sp)
                                generatedProposal.tipsForWinning.forEach { tip ->
                                    Row(modifier = Modifier.padding(top = 3.dp), verticalAlignment = Alignment.Top) {
                                        Icon(Icons.Default.Star, contentDescription = null, tint = colors.primary, modifier = Modifier.size(14.dp))
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(tip, color = colors.textSecondary, fontSize = (11 * fontScale).sp)
                                    }
                                }
                            }
                        }
                    }
                }
            }

            AiSubTab.INTERVIEW_PREP -> {
                // Interview scenarios
                items(interviewScenarios) { item ->
                    var isExpanded by remember { mutableStateOf(false) }

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(1.dp, colors.cardBorder, RoundedCornerShape(16.dp))
                            .clickable { isExpanded = !isExpanded },
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = colors.cardBackground)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(colors.surfaceVariant)
                                        .padding(horizontal = 8.dp, vertical = 3.dp)
                                ) {
                                    Text(item.category, color = colors.primary, fontSize = (11 * fontScale).sp, fontWeight = FontWeight.Bold)
                                }

                                Icon(
                                    imageVector = if (isExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                                    contentDescription = null,
                                    tint = colors.textSecondary
                                )
                            }

                            Text(
                                text = item.question,
                                color = colors.textPrimary,
                                fontSize = (13 * fontScale).sp,
                                fontWeight = FontWeight.SemiBold,
                                modifier = Modifier.padding(top = 8.dp)
                            )

                            AnimatedVisibility(visible = isExpanded) {
                                Column(modifier = Modifier.padding(top = 10.dp)) {
                                    Text("🎯 نیت واقعی کارفرما از این سوال:", color = colors.primary, fontSize = (12 * fontScale).sp, fontWeight = FontWeight.Bold)
                                    Text(item.intentBehindQuestion, color = colors.textSecondary, fontSize = (11 * fontScale).sp, modifier = Modifier.padding(top = 2.dp, bottom = 8.dp))

                                    Text("💡 پاسخ ایده‌آل و برنده:", color = colors.primary, fontSize = (12 * fontScale).sp, fontWeight = FontWeight.Bold)
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(colors.inputBackground)
                                            .border(1.dp, colors.cardBorder, RoundedCornerShape(8.dp))
                                            .padding(10.dp)
                                    ) {
                                        Text(item.sampleIdealAnswer, color = colors.textPrimary, fontSize = (11 * fontScale).sp, lineHeight = (17 * fontScale).sp)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
