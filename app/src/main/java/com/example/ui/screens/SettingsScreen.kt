package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.model.*
import com.example.ui.theme.*

@Composable
fun SettingsScreen(
    appSettings: AppSettings,
    userCredits: Int,
    isVip: Boolean,
    onLanguageChange: (AppLanguage) -> Unit,
    onFontScaleChange: (FontScale) -> Unit,
    onThemeColorChange: (ThemeColorChoice) -> Unit,
    onDarkModeToggle: (Boolean) -> Unit,
    onPaymentGatewayChange: (PaymentGateway) -> Unit,
    onShowInterstitialAd: () -> Unit,
    onShowRewardedAd: () -> Unit,
    onPurchasePlan: (PaymentGateway, String) -> Unit
) {
    val colors = LocalAppColors.current
    val fontScale = LocalFontScale.current.scale
    val lang = appSettings.language

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(top = 12.dp, bottom = 96.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Top Header Card
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.5.dp, colors.primary.copy(alpha = 0.5f), RoundedCornerShape(18.dp)),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = colors.cardBackground)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(46.dp)
                            .clip(CircleShape)
                            .background(Brush.linearGradient(listOf(colors.primary, colors.secondary))),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = null,
                            tint = Navy900,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = AppStrings.settingsTitle(lang),
                            color = colors.textPrimary,
                            fontSize = (16 * fontScale).sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = if (lang == AppLanguage.PERSIAN) "شخصی‌سازی ظاهر، تم کامل، سایز متن و زبان" else "Customize appearance, theme, text size & language",
                            color = colors.textSecondary,
                            fontSize = (11 * fontScale).sp
                        )
                    }
                }
            }
        }

        // 1. Language Switcher (Persian & English)
        item {
            SettingsSectionCard(
                title = AppStrings.languageSection(lang),
                icon = Icons.Default.Language,
                colors = colors,
                fontScale = fontScale
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    AppLanguage.values().forEach { l ->
                        val isSelected = l == appSettings.language
                        Card(
                            modifier = Modifier
                                .weight(1f)
                                .clickable { onLanguageChange(l) }
                                .border(
                                    1.5.dp,
                                    if (isSelected) colors.primary else Color.Transparent,
                                    RoundedCornerShape(12.dp)
                                ),
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = if (isSelected) colors.primary.copy(alpha = 0.15f)
                                else colors.surfaceVariant
                            )
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 12.dp, horizontal = 10.dp),
                                horizontalArrangement = Arrangement.Center,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                if (isSelected) {
                                    Icon(
                                        imageVector = Icons.Default.CheckCircle,
                                        contentDescription = null,
                                        tint = colors.primary,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                }
                                Text(
                                    text = if (lang == AppLanguage.PERSIAN) l.titleFa else l.titleEn,
                                    color = if (isSelected) colors.primary else colors.textPrimary,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                    fontSize = (13 * fontScale).sp
                                )
                            }
                        }
                    }
                }
            }
        }

        // 2. Font Scale Selector (Small, Medium, Large, Extra Large)
        item {
            SettingsSectionCard(
                title = AppStrings.fontSizeSection(lang),
                icon = Icons.Default.FormatSize,
                colors = colors,
                fontScale = fontScale
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        FontScale.values().forEach { scale ->
                            val isSelected = scale == appSettings.fontScale
                            FilterChip(
                                selected = isSelected,
                                onClick = { onFontScaleChange(scale) },
                                label = {
                                    Text(
                                        text = if (lang == AppLanguage.PERSIAN) scale.titleFa.split(" ")[0] else scale.titleEn,
                                        fontSize = (11 * fontScale).sp,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                    )
                                },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = colors.primary,
                                    selectedLabelColor = Navy900,
                                    containerColor = colors.surfaceVariant,
                                    labelColor = colors.textSecondary
                                )
                            )
                        }
                    }

                    // Live preview of the text size
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(colors.inputBackground)
                            .border(1.dp, colors.cardBorder, RoundedCornerShape(8.dp))
                            .padding(10.dp)
                    ) {
                        Text(
                            text = if (lang == AppLanguage.PERSIAN)
                                "پیش‌نمایش زنده: تمام متون رزومه‌ها، عنوان‌ها و دکمه‌ها در این مقیاس تنظیم می‌شوند."
                            else
                                "Live Preview: All resume texts, headlines and buttons will scale to this size.",
                            color = colors.textPrimary,
                            fontSize = (12 * fontScale).sp,
                            lineHeight = (18 * fontScale).sp
                        )
                    }
                }
            }
        }

        // 3. Theme Colors (7 distinct vibrant accents)
        item {
            SettingsSectionCard(
                title = AppStrings.themeColorSection(lang),
                icon = Icons.Default.Palette,
                colors = colors,
                fontScale = fontScale
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        text = if (lang == AppLanguage.PERSIAN)
                            "تم رنگی فعال: ${appSettings.themeColor.titleFa}"
                        else
                            "Active Accent: ${appSettings.themeColor.titleEn}",
                        color = colors.primary,
                        fontSize = (13 * fontScale).sp,
                        fontWeight = FontWeight.Bold
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        ThemeColorChoice.values().forEach { choice ->
                            val isSelected = choice == appSettings.themeColor
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(CircleShape)
                                    .background(choice.primaryColor)
                                    .border(
                                        width = if (isSelected) 3.dp else 1.dp,
                                        color = if (isSelected) colors.textPrimary else Color.Transparent,
                                        shape = CircleShape
                                    )
                                    .clickable { onThemeColorChange(choice) },
                                contentAlignment = Alignment.Center
                            ) {
                                if (isSelected) {
                                    Icon(
                                        imageVector = Icons.Default.Check,
                                        contentDescription = null,
                                        tint = Navy900,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // 4. Dark / Light Mode Switch
        item {
            SettingsSectionCard(
                title = AppStrings.darkModeSection(lang),
                icon = if (appSettings.isDarkMode) Icons.Default.DarkMode else Icons.Default.LightMode,
                colors = colors,
                fontScale = fontScale
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = if (appSettings.isDarkMode)
                                (if (lang == AppLanguage.PERSIAN) "حالت شب (Dark Mode) فعال است" else "Dark Mode is Active")
                            else
                                (if (lang == AppLanguage.PERSIAN) "حالت روز (Light Mode) فعال است" else "Light Mode is Active"),
                            color = colors.textPrimary,
                            fontSize = (13 * fontScale).sp,
                            fontWeight = FontWeight.Medium
                        )
                        Text(
                            text = if (lang == AppLanguage.PERSIAN) "تغییر کامل رنگ قاب، کارت‌ها و پس‌زمینه" else "Completely shifts backgrounds, frame & cards",
                            color = colors.textSecondary,
                            fontSize = (11 * fontScale).sp
                        )
                    }

                    Switch(
                        checked = appSettings.isDarkMode,
                        onCheckedChange = onDarkModeToggle,
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = colors.primary,
                            checkedTrackColor = colors.primary.copy(alpha = 0.3f),
                            uncheckedThumbColor = Color.Gray,
                            uncheckedTrackColor = Color.LightGray
                        )
                    )
                }
            }
        }

        // 5. In-App Ads Integration Test (Tapsell / Tapsi)
        item {
            SettingsSectionCard(
                title = AppStrings.adTestSection(lang),
                icon = Icons.Default.Campaign,
                colors = colors,
                fontScale = fontScale
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = if (lang == AppLanguage.PERSIAN)
                            "ماژول تبلیغات هوشمند تپسی / تپسل با رویکرد پاداش‌محور (Rewarded) بدون مزاحمت:"
                        else
                            "Tapsi / Tapsell ad module with rewarded non-intrusive approach:",
                        color = colors.textSecondary,
                        fontSize = (11 * fontScale).sp
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedButton(
                            onClick = onShowInterstitialAd,
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(10.dp),
                            border = ButtonDefaults.outlinedButtonBorder(enabled = true).copy(brush = Brush.horizontalGradient(listOf(colors.primary, colors.secondary)))
                        ) {
                            Text(
                                text = if (lang == AppLanguage.PERSIAN) "تبلیغ بین‌صفحه‌ای" else "Interstitial Ad",
                                fontSize = (11 * fontScale).sp,
                                color = colors.primary,
                                fontWeight = FontWeight.SemiBold
                            )
                        }

                        Button(
                            onClick = onShowRewardedAd,
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = colors.primary, contentColor = Navy900)
                        ) {
                            Icon(Icons.Default.PlayCircle, contentDescription = null, modifier = Modifier.size(15.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = if (lang == AppLanguage.PERSIAN) "ویدیوی جایزه‌ای (+۳)" else "Rewarded Video (+3)",
                                fontSize = (11 * fontScale).sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }

        // 6. In-App Purchase Gateways (Cafe Bazaar & Myket)
        item {
            SettingsSectionCard(
                title = AppStrings.storeBillingSection(lang),
                icon = Icons.Default.ShoppingBag,
                colors = colors,
                fontScale = fontScale
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        text = if (lang == AppLanguage.PERSIAN) "انتخاب درگاه پرداخت درون‌برنامه‌ای:" else "Select Payment Store Gateway:",
                        color = colors.textPrimary,
                        fontSize = (12 * fontScale).sp,
                        fontWeight = FontWeight.Bold
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        PaymentGateway.values().forEach { gateway ->
                            val isSelected = gateway == appSettings.paymentGateway
                            Card(
                                modifier = Modifier
                                    .weight(1f)
                                    .clickable { onPaymentGatewayChange(gateway) }
                                    .border(
                                        1.5.dp,
                                        if (isSelected) colors.primary else Color.Transparent,
                                        RoundedCornerShape(10.dp)
                                    ),
                                shape = RoundedCornerShape(10.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = if (isSelected) colors.primary.copy(alpha = 0.15f)
                                    else colors.surfaceVariant
                                )
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 10.dp, horizontal = 8.dp),
                                    horizontalArrangement = Arrangement.Center,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Storefront,
                                        contentDescription = null,
                                        tint = if (isSelected) colors.primary else Color.Gray,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = if (lang == AppLanguage.PERSIAN) gateway.titleFa else gateway.titleEn,
                                        fontSize = (11 * fontScale).sp,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                        color = if (isSelected) colors.primary else colors.textPrimary
                                    )
                                }
                            }
                        }
                    }

                    // Plan selection
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(colors.inputBackground)
                            .border(1.dp, colors.cardBorder, RoundedCornerShape(12.dp))
                            .padding(12.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        SubscriptionPlanRow(
                            title = if (lang == AppLanguage.PERSIAN) "اشتراک ۱ ماهه فریلنس‌پرو" else "1-Month VIP Plan",
                            price = if (lang == AppLanguage.PERSIAN) "قیمت در فروشگاه" else "Price in store",
                            isBestValue = false,
                            colors = colors,
                            fontScale = fontScale,
                            onClick = { onPurchasePlan(appSettings.paymentGateway, "۱ ماهه") }
                        )

                        SubscriptionPlanRow(
                            title = if (lang == AppLanguage.PERSIAN) "اشتراک ۳ ماهه فریلنس‌پرو" else "3-Month VIP Plan",
                            price = if (lang == AppLanguage.PERSIAN) "قیمت در فروشگاه" else "Price in store",
                            isBestValue = false,
                            colors = colors,
                            fontScale = fontScale,
                            onClick = { onPurchasePlan(appSettings.paymentGateway, "۳ ماهه") }
                        )

                        SubscriptionPlanRow(
                            title = if (lang == AppLanguage.PERSIAN) "اشتراک سالانه (تخفیف ویژه VIP)" else "1-Year VIP (Best Value)",
                            price = if (lang == AppLanguage.PERSIAN) "قیمت در فروشگاه" else "Price in store",
                            isBestValue = true,
                            colors = colors,
                            fontScale = fontScale,
                            onClick = { onPurchasePlan(appSettings.paymentGateway, "سالانه") }
                        )
                    }
                }
            }
        }

        // 7. About Us Section (Dedicated Developer, Version, Icon)
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(
                        1.5.dp,
                        colors.primary.copy(alpha = 0.5f),
                        RoundedCornerShape(20.dp)
                    ),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = colors.cardBackground)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // App Icon with glowing aura
                    Box(
                        modifier = Modifier
                            .size(72.dp)
                            .clip(RoundedCornerShape(18.dp))
                            .background(Brush.linearGradient(listOf(colors.primary, colors.secondary)))
                            .padding(2.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .clip(RoundedCornerShape(16.dp))
                                .background(Navy900),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Work,
                                contentDescription = "آیکون برنامه",
                                tint = colors.primary,
                                modifier = Modifier.size(38.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = AppStrings.appTitle(lang),
                        color = colors.textPrimary,
                        fontSize = (18 * fontScale).sp,
                        fontWeight = FontWeight.Bold
                    )

                    Text(
                        text = AppStrings.appVersion(lang),
                        color = colors.primary,
                        fontSize = (12 * fontScale).sp,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.padding(top = 2.dp, bottom = 12.dp)
                    )

                    // Developer Card
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(colors.surfaceVariant)
                            .padding(horizontal = 14.dp, vertical = 10.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = if (lang == AppLanguage.PERSIAN) "طراح و توسعه‌دهنده:" else "Developer:",
                            color = colors.textSecondary,
                            fontSize = (12 * fontScale).sp
                        )

                        Text(
                            text = AppStrings.developerName(lang),
                            color = colors.textPrimary,
                            fontWeight = FontWeight.Bold,
                            fontSize = (13 * fontScale).sp
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = AppStrings.appDescription(lang),
                        color = colors.textSecondary,
                        fontSize = (11 * fontScale).sp,
                        lineHeight = (17 * fontScale).sp,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Verified,
                            contentDescription = null,
                            tint = Emerald500,
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            text = if (lang == AppLanguage.PERSIAN) "آماده برای بیلد نهایی GitHub (APK و AAB)" else "Ready for GitHub Release Build (APK & AAB)",
                            color = Emerald500,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = (11 * fontScale).sp
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun SettingsSectionCard(
    title: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    colors: AppColors,
    fontScale: Float,
    content: @Composable () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, colors.cardBorder, RoundedCornerShape(16.dp)),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = colors.cardBackground)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(bottom = 12.dp)
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = colors.primary,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = title,
                    color = colors.textPrimary,
                    fontSize = (14 * fontScale).sp,
                    fontWeight = FontWeight.Bold
                )
            }

            content()
        }
    }
}

@Composable
private fun SubscriptionPlanRow(
    title: String,
    price: String,
    isBestValue: Boolean,
    colors: AppColors,
    fontScale: Float,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .clickable(onClick = onClick)
            .padding(vertical = 6.dp, horizontal = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(title, color = colors.textPrimary, fontSize = (12 * fontScale).sp, fontWeight = FontWeight.SemiBold)
                if (isBestValue) {
                    Spacer(modifier = Modifier.width(6.dp))
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .background(Emerald500)
                            .padding(horizontal = 4.dp, vertical = 2.dp)
                    ) {
                        Text("۵۰٪ تخفیف", color = Navy900, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
            Text(price, color = colors.primary, fontSize = (11 * fontScale).sp, fontWeight = FontWeight.Bold)
        }

        Button(
            onClick = onClick,
            shape = RoundedCornerShape(8.dp),
            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
            colors = ButtonDefaults.buttonColors(containerColor = colors.primary, contentColor = Navy900)
        ) {
            Text("خرید", fontSize = (11 * fontScale).sp, fontWeight = FontWeight.Bold)
        }
    }
}
