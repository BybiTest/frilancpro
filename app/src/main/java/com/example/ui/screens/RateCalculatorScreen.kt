package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.RateCalculationOutput
import com.example.model.RateCalculatorInput
import com.example.ui.theme.*
import java.text.NumberFormat
import java.util.Locale

@Composable
fun RateCalculatorScreen(
    rateInput: RateCalculatorInput,
    rateOutput: RateCalculationOutput,
    onInputChange: (RateCalculatorInput) -> Unit
) {
    val colors = LocalAppColors.current
    val fontScale = LocalFontScale.current.scale
    val numberFormat = NumberFormat.getNumberInstance(Locale.US)

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(top = 12.dp, bottom = 96.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Output Hero Card
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
                        Text("نرخ‌گذاری استاندارد خدمات فریلنسری", color = colors.primary, fontWeight = FontWeight.Bold, fontSize = (14 * fontScale).sp)
                        Icon(Icons.Default.Calculate, contentDescription = null, tint = colors.primary, modifier = Modifier.size(22.dp))
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        RateMetricBox(
                            title = "نرخ ساعتی منصفانه",
                            value = "${numberFormat.format(rateOutput.hourlyRateToman)} ت",
                            modifier = Modifier.weight(1f),
                            highlightColor = colors.primary,
                            colors = colors,
                            fontScale = fontScale
                        )
                        RateMetricBox(
                            title = "نرخ روزانه (۶ ساعت کار)",
                            value = "${numberFormat.format(rateOutput.dailyRateToman)} ت",
                            modifier = Modifier.weight(1f),
                            highlightColor = colors.secondary,
                            colors = colors,
                            fontScale = fontScale
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        RateMetricBox(
                            title = "کف قیمت هر پروژه",
                            value = "${numberFormat.format(rateOutput.minimumProjectRateToman)} ت",
                            modifier = Modifier.weight(1f),
                            highlightColor = colors.textPrimary,
                            colors = colors,
                            fontScale = fontScale
                        )
                        RateMetricBox(
                            title = "هدف درآمد ناخالص سالانه",
                            value = "${numberFormat.format(rateOutput.totalAnnualTargetToman / 1_000_000L)} م ت",
                            modifier = Modifier.weight(1f),
                            highlightColor = Coral500,
                            colors = colors,
                            fontScale = fontScale
                        )
                    }

                    // Strategic Advice
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 12.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(colors.surfaceVariant)
                            .padding(10.dp),
                        verticalAlignment = Alignment.Top
                    ) {
                        Icon(Icons.Default.Info, contentDescription = null, tint = colors.primary, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = rateOutput.adviceText,
                            color = colors.textPrimary,
                            fontSize = (11 * fontScale).sp,
                            lineHeight = (16 * fontScale).sp
                        )
                    }
                }
            }
        }

        // Sliders & Customization Card
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, colors.cardBorder, RoundedCornerShape(18.dp)),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = colors.cardBackground)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Text("تنظیم پارامترهای فرمول قیمت‌گذاری", color = colors.textPrimary, fontSize = (14 * fontScale).sp, fontWeight = FontWeight.Bold)

                    Spacer(modifier = Modifier.height(14.dp))

                    // Living Expenses
                    SliderWithLabel(
                        title = "هزینه‌های زندگی ماهانه (اجاره، خوراک، قبض‌ها)",
                        valueText = "${numberFormat.format(rateInput.monthlyLivingExpenses / 1_000_000L)} میلیون تومان",
                        value = (rateInput.monthlyLivingExpenses / 1_000_000L).toFloat(),
                        range = 10f..60f,
                        colors = colors,
                        fontScale = fontScale,
                        onValueChange = {
                            onInputChange(rateInput.copy(monthlyLivingExpenses = (it.toLong()) * 1_000_000L))
                        }
                    )

                    // Business Expenses
                    SliderWithLabel(
                        title = "هزینه‌های ماهانه بیزینس (اینترنت، ابزارها، استهلاک سیستم)",
                        valueText = "${numberFormat.format(rateInput.businessExpenses / 1_000_000L)} میلیون تومان",
                        value = (rateInput.businessExpenses / 1_000_000L).toFloat(),
                        range = 1f..20f,
                        colors = colors,
                        fontScale = fontScale,
                        onValueChange = {
                            onInputChange(rateInput.copy(businessExpenses = (it.toLong()) * 1_000_000L))
                        }
                    )

                    // Desired Savings / Profit Margin
                    SliderWithLabel(
                        title = "حاشیه سود و پس‌انداز مطلوب برای روز مبادا",
                        valueText = "${rateInput.desiredSavingsMarginPercent}٪ سود خالص",
                        value = rateInput.desiredSavingsMarginPercent.toFloat(),
                        range = 10f..50f,
                        colors = colors,
                        fontScale = fontScale,
                        onValueChange = {
                            onInputChange(rateInput.copy(desiredSavingsMarginPercent = it.toInt()))
                        }
                    )

                    // Billable Hours per Week
                    SliderWithLabel(
                        title = "ساعات کار مفید و قابل فاکتور در هفته (Billable Hours)",
                        valueText = "${rateInput.billableHoursPerWeek} ساعت کار واقعی روی پروژه",
                        value = rateInput.billableHoursPerWeek.toFloat(),
                        range = 15f..40f,
                        colors = colors,
                        fontScale = fontScale,
                        onValueChange = {
                            onInputChange(rateInput.copy(billableHoursPerWeek = it.toInt()))
                        }
                    )

                    // Vacation Weeks
                    SliderWithLabel(
                        title = "هفته‌های مرخصی و تعطیلات در طول سال",
                        valueText = "${rateInput.vacationWeeksPerYear} هفته استراحت / بیماری",
                        value = rateInput.vacationWeeksPerYear.toFloat(),
                        range = 1f..8f,
                        colors = colors,
                        fontScale = fontScale,
                        onValueChange = {
                            onInputChange(rateInput.copy(vacationWeeksPerYear = it.toInt()))
                        }
                    )
                }
            }
        }

        // Education Note
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, colors.cardBorder, RoundedCornerShape(14.dp)),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = colors.cardBackground)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text("💡 اصل طلایی فریلنسری در قیمت‌گذاری:", color = colors.primary, fontWeight = FontWeight.Bold, fontSize = (12 * fontScale).sp)
                    Text(
                        "فریلنسرها کارمند نیستند که بتوانند ۴۰ ساعت در هفته را برای کارفرما کار کنند. حدود ۳۰ تا ۴۰ درصد از وقت هر فریلنسر صرف بازاریابی، مذاکره، پاسخ به ایمیل‌ها و فاکتورنویسی می‌شود که کارفرما بابتش پولی نمی‌دهد؛ به همین دلیل نرخ ساعتی فریلنسر باید بالاتر از حقوق یک کارمند تمام‌وقت محاسبه شود.",
                        color = colors.textSecondary,
                        fontSize = (11 * fontScale).sp,
                        lineHeight = (17 * fontScale).sp,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun RateMetricBox(
    title: String,
    value: String,
    modifier: Modifier = Modifier,
    highlightColor: Color,
    colors: AppColors,
    fontScale: Float
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(colors.inputBackground)
            .border(1.dp, colors.cardBorder, RoundedCornerShape(12.dp))
            .padding(12.dp)
    ) {
        Column {
            Text(title, color = colors.textSecondary, fontSize = (10 * fontScale).sp)
            Spacer(modifier = Modifier.height(4.dp))
            Text(value, color = highlightColor, fontSize = (14 * fontScale).sp, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
private fun SliderWithLabel(
    title: String,
    valueText: String,
    value: Float,
    range: ClosedFloatingPointRange<Float>,
    colors: AppColors,
    fontScale: Float,
    onValueChange: (Float) -> Unit
) {
    Column(modifier = Modifier.padding(vertical = 6.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(title, color = colors.textPrimary, fontSize = (11 * fontScale).sp)
            Text(valueText, color = colors.primary, fontSize = (11 * fontScale).sp, fontWeight = FontWeight.Bold)
        }
        Slider(
            value = value,
            onValueChange = onValueChange,
            valueRange = range,
            colors = SliderDefaults.colors(
                thumbColor = colors.primary,
                activeTrackColor = colors.primary,
                inactiveTrackColor = colors.surfaceVariant
            )
        )
    }
}
