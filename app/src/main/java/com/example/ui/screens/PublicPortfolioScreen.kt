package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
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
import com.example.model.PortfolioProject
import com.example.ui.theme.*

@Composable
fun PublicPortfolioScreen(
    portfolioProjects: List<PortfolioProject>,
    onAddProjectClick: () -> Unit
) {
    val colors = LocalAppColors.current
    val fontScale = LocalFontScale.current.scale

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(top = 12.dp, bottom = 96.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Local portfolio preview. Public hosting is not configured in this app yet.
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.5.dp, colors.primary.copy(alpha = 0.5f), RoundedCornerShape(20.dp)),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = colors.cardBackground)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(18.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(10.dp)
                                    .clip(CircleShape)
                                    .background(colors.primary)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("پیش‌نمایش داخل برنامه", color = colors.primary, fontSize = (12 * fontScale).sp, fontWeight = FontWeight.Bold)
                        }

                        Text("انتشار آنلاین فعال نیست", color = colors.textSecondary, fontSize = (11 * fontScale).sp)
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = "مدیریت پورتفولیو و نمونه‌کارها",
                        color = colors.textPrimary,
                        fontSize = (16 * fontScale).sp,
                        fontWeight = FontWeight.Bold
                    )

                    Text(
                        text = "نمونه‌کارهای ثبت‌شده در این صفحه فقط داخل برنامه قابل مشاهده‌اند. برای انتشار لینک عمومی، سرویس میزبانی و انتشار آنلاین باید جداگانه راه‌اندازی شود.",
                        color = colors.textSecondary,
                        fontSize = (12 * fontScale).sp,
                        lineHeight = (18 * fontScale).sp,
                        modifier = Modifier.padding(top = 4.dp, bottom = 12.dp)
                    )

                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        color = colors.surfaceVariant
                    ) {
                        Text(
                            text = "وضعیت انتشار: هنوز راه‌اندازی نشده است",
                            color = colors.textSecondary,
                            fontSize = (12 * fontScale).sp,
                            modifier = Modifier.padding(12.dp)
                        )
                    }
                }
            }
        }

        // Section Title: Featured Projects
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "پروژه‌های شاخص در ویترین (${portfolioProjects.size})",
                    color = colors.textPrimary,
                    fontSize = (15 * fontScale).sp,
                    fontWeight = FontWeight.Bold
                )

                TextButton(onClick = onAddProjectClick) {
                    Icon(Icons.Default.Add, contentDescription = null, tint = colors.primary, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("افزودن نمونه‌کار", color = colors.primary, fontSize = (12 * fontScale).sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        if (portfolioProjects.isEmpty()) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = colors.cardBackground)
                ) {
                    Text(
                        text = "هنوز نمونه‌کاری ثبت نشده است. فقط پروژه‌های واقعی خود را اضافه کنید.",
                        color = colors.textSecondary,
                        fontSize = (13 * fontScale).sp,
                        modifier = Modifier.padding(18.dp)
                    )
                }
            }
        }

        // Projects Cards
        items(portfolioProjects) { proj ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, colors.cardBorder, RoundedCornerShape(16.dp)),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = colors.cardBackground)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(proj.title, color = colors.textPrimary, fontSize = (14 * fontScale).sp, fontWeight = FontWeight.Bold)
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(colors.surfaceVariant)
                                .padding(horizontal = 8.dp, vertical = 3.dp)
                        ) {
                            Text(proj.category, color = colors.primary, fontSize = (11 * fontScale).sp, fontWeight = FontWeight.SemiBold)
                        }
                    }

                    Text("کارفرما: ${proj.clientName}", color = colors.textSecondary, fontSize = (12 * fontScale).sp, modifier = Modifier.padding(top = 2.dp))
                    Text(proj.description, color = colors.textPrimary, fontSize = (12 * fontScale).sp, modifier = Modifier.padding(vertical = 6.dp))

                    // Metric Banner
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(colors.surfaceVariant)
                            .padding(horizontal = 10.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.AutoMirrored.Filled.TrendingUp, contentDescription = null, tint = colors.primary, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("نتیجه تجاری: ${proj.metrics}", color = colors.primary, fontSize = (11 * fontScale).sp, fontWeight = FontWeight.SemiBold)
                    }

                    // Tags
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 10.dp),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        proj.tags.forEach { tag ->
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(colors.surfaceVariant)
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Text("#$tag", color = colors.textSecondary, fontSize = (10 * fontScale).sp)
                            }
                        }
                    }
                }
            }
        }


    }
}

