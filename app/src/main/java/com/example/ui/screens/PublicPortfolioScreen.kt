package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.TrendingUp
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
import com.example.model.UserProfile
import com.example.ui.theme.*

@Composable
fun PublicPortfolioScreen(
    userProfile: UserProfile,
    portfolioProjects: List<PortfolioProject>,
    onAddProjectClick: () -> Unit,
    onCopyLink: () -> Unit
) {
    val colors = LocalAppColors.current
    val fontScale = LocalFontScale.current.scale
    var showCopiedToast by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(top = 12.dp, bottom = 96.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Public Link Card (Shareable with Clients)
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
                            Text("ویترین آنلاین شما فعال است", color = colors.primary, fontSize = (12 * fontScale).sp, fontWeight = FontWeight.Bold)
                        }

                        Text("نسخه وب زنده", color = colors.textSecondary, fontSize = (11 * fontScale).sp)
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = "صفحه عمومی نمونه‌کارها (Public Portfolio)",
                        color = colors.textPrimary,
                        fontSize = (16 * fontScale).sp,
                        fontWeight = FontWeight.Bold
                    )

                    Text(
                        text = "این آدرس را به کارفرماها در پونیشا، کارلنسر یا تلگرام بفرستید تا نمونه‌کارهای تاییدشده شما را بدون نیاز به لاگین ببینند.",
                        color = colors.textSecondary,
                        fontSize = (12 * fontScale).sp,
                        lineHeight = (18 * fontScale).sp,
                        modifier = Modifier.padding(top = 4.dp, bottom = 12.dp)
                    )

                    // URL Box
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(colors.inputBackground)
                            .border(1.dp, colors.cardBorder, RoundedCornerShape(12.dp))
                            .padding(horizontal = 12.dp, vertical = 10.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "https://freelancepro.me/@${userProfile.publicPortfolioSlug}",
                            color = colors.primary,
                            fontSize = (13 * fontScale).sp,
                            fontWeight = FontWeight.SemiBold
                        )

                        Button(
                            onClick = {
                                onCopyLink()
                                showCopiedToast = true
                            },
                            shape = RoundedCornerShape(8.dp),
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = colors.primary, contentColor = Navy900),
                            modifier = Modifier.testTag("copy_portfolio_link_btn")
                        ) {
                            Icon(Icons.Default.ContentCopy, contentDescription = null, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(if (showCopiedToast) "کپی شد!" else "کپی لینک", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }

                    // Analytics metrics
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 14.dp),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        MetricItem(title = "بازدید کارفرماها", value = "${userProfile.portfolioViewsCount}", colors = colors, fontScale = fontScale)
                        MetricItem(title = "کلیک روی تماس", value = "${userProfile.clientContactCount}", colors = colors, fontScale = fontScale)
                        MetricItem(title = "وضعیت فریلنسر", value = "🟢 در دسترس", colors = colors, fontScale = fontScale)
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

        // Testimonial / Social Proof
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, colors.cardBorder, RoundedCornerShape(16.dp)),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = colors.cardBackground)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("رضایت‌نامه کارفرمایان قبلی (Testimonials)", color = colors.textPrimary, fontSize = (13 * fontScale).sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        "«همکاری با علی رضایی یکی از بهترین تجربیات ما در طراحی محصول بود. سرعت بالا، درک عمیق از بیزینس و تحویل کامپوننت‌های بدون نقص.»",
                        color = colors.textPrimary,
                        fontSize = (12 * fontScale).sp,
                        lineHeight = (18 * fontScale).sp
                    )
                    Text("— مدیر محصول شرکت مالی آریا ⭐⭐⭐⭐⭐", color = colors.primary, fontSize = (11 * fontScale).sp, modifier = Modifier.padding(top = 6.dp))
                }
            }
        }
    }
}

@Composable
private fun MetricItem(title: String, value: String, colors: AppColors, fontScale: Float) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(value, color = colors.textPrimary, fontWeight = FontWeight.Bold, fontSize = (13 * fontScale).sp)
        Text(title, color = colors.textSecondary, fontSize = (10 * fontScale).sp)
    }
}
