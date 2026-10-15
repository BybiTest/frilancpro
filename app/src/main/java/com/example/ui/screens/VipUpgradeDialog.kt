package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.ui.theme.*

@Composable
fun VipUpgradeDialog(
    currentCredits: Int,
    onDismiss: () -> Unit,
    onUpgrade: () -> Unit,
    onWatchAd: () -> Unit
) {
    val colors = LocalAppColors.current
    val fontScale = LocalFontScale.current.scale

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
                .clip(RoundedCornerShape(24.dp))
                .border(1.5.dp, colors.primary, RoundedCornerShape(24.dp))
                .testTag("vip_upgrade_dialog"),
            color = colors.cardBackground
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Header Icon
                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .clip(CircleShape)
                        .background(
                            Brush.linearGradient(
                                listOf(colors.primary, colors.secondary)
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.WorkspacePremium,
                        contentDescription = "VIP",
                        tint = Navy900,
                        modifier = Modifier.size(36.dp)
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = "ارتقا به فریلنسر حرفه‌ای VIP",
                    color = colors.textPrimary,
                    fontSize = (19 * fontScale).sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )

                Text(
                    text = "اعتبار نامحدود هوش مصنوعی در زمان فعال بودن اشتراک و مدیریت پورتفولیو",
                    color = colors.textSecondary,
                    fontSize = (13 * fontScale).sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(top = 4.dp, bottom = 16.dp)
                )

                // Feature items
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(colors.surfaceVariant)
                        .padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    VipFeatureRow(text = "پروپوزال‌نویسی نامحدود با هوش مصنوعی (Gemini)", colors = colors, fontScale = fontScale)
                    VipFeatureRow(text = "اسکنر دقیق ATS برای برنده شدن در استخدام", colors = colors, fontScale = fontScale)
                    VipFeatureRow(text = "مدیریت و پیش‌نمایش نمونه‌کارها در پورتفولیو", colors = colors, fontScale = fontScale)
                    VipFeatureRow(text = "بازیابی وضعیت اشتراک از طریق فروشگاه", colors = colors, fontScale = fontScale)
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Price Tag Card
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = colors.surfaceVariant)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "اشتراک ویژه سالانه",
                                color = colors.primary,
                                fontSize = (13 * fontScale).sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "قیمت نهایی در صفحه پرداخت فروشگاه نمایش داده می‌شود.",
                                color = colors.textPrimary,
                                fontSize = (12 * fontScale).sp
                            )
                        }
                        Text(
                            text = "طرح سالانه؛ طبق شرایط فروشگاه",
                            color = Emerald500,
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = (12 * fontScale).sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Primary Upgrade Button
                Button(
                    onClick = onUpgrade,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("buy_vip_button"),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = colors.primary,
                        contentColor = Navy900
                    )
                ) {
                    Icon(imageVector = Icons.Default.Star, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("خرید اشتراک VIP و فعال‌سازی فوری", fontWeight = FontWeight.Bold, fontSize = (14 * fontScale).sp)
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Freemium Rewarded Ad Option
                OutlinedButton(
                    onClick = onWatchAd,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(44.dp)
                        .testTag("watch_ad_credits_button"),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = colors.primary),
                    border = ButtonDefaults.outlinedButtonBorder(enabled = true).copy(brush = Brush.horizontalGradient(listOf(colors.primary, colors.secondary)))
                ) {
                    Icon(imageVector = Icons.Default.PlayCircle, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("تماشای ویدیوی تبلیغاتی (+۳ اعتبار رایگان)", fontSize = (13 * fontScale).sp, fontWeight = FontWeight.Medium)
                }

                Spacer(modifier = Modifier.height(8.dp))

                TextButton(onClick = onDismiss) {
                    Text("شاید بعداً", color = colors.textSecondary, fontSize = (12 * fontScale).sp)
                }
            }
        }
    }
}

@Composable
private fun VipFeatureRow(text: String, colors: AppColors, fontScale: Float) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.Default.CheckCircle,
            contentDescription = null,
            tint = Emerald500,
            modifier = Modifier.size(16.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = text,
            color = colors.textPrimary,
            fontSize = (12 * fontScale).sp
        )
    }
}
