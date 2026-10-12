package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import com.example.model.AppSettings
import com.example.model.FontScale
import com.example.model.ThemeColorChoice

data class AppColors(
    val isDark: Boolean,
    val primary: Color,
    val secondary: Color,
    val background: Color,
    val surface: Color,
    val surfaceVariant: Color,
    val cardBackground: Color,
    val cardBorder: Color,
    val textPrimary: Color,
    val textSecondary: Color,
    val inputBackground: Color,
    val navBackground: Color,
    val headerBackground: Color
)

fun getAppColors(appSettings: AppSettings): AppColors {
    val isDark = appSettings.isDarkMode
    val p = appSettings.themeColor.primaryColor
    val s = appSettings.themeColor.secondaryColor

    return if (isDark) {
        AppColors(
            isDark = true,
            primary = p,
            secondary = s,
            background = Navy900,
            surface = Navy800,
            surfaceVariant = Navy700,
            cardBackground = Navy800,
            cardBorder = p.copy(alpha = 0.35f),
            textPrimary = Color(0xFFF8FAFC),
            textSecondary = Color(0xFF94A3B8),
            inputBackground = Navy900,
            navBackground = Navy800,
            headerBackground = Navy900.copy(alpha = 0.95f)
        )
    } else {
        AppColors(
            isDark = false,
            primary = p,
            secondary = s,
            background = Color(0xFFF1F5F9),
            surface = Color(0xFFFFFFFF),
            surfaceVariant = Color(0xFFE2E8F0),
            cardBackground = Color(0xFFFFFFFF),
            cardBorder = p.copy(alpha = 0.40f),
            textPrimary = Color(0xFF0F172A),
            textSecondary = Color(0xFF64748B),
            inputBackground = Color(0xFFF8FAFC),
            navBackground = Color(0xFFFFFFFF),
            headerBackground = Color(0xFFFFFFFF).copy(alpha = 0.95f)
        )
    }
}

val LocalAppColors = staticCompositionLocalOf { getAppColors(AppSettings()) }
val LocalFontScale = staticCompositionLocalOf { FontScale.MEDIUM }

fun getDynamicDarkColorScheme(choice: ThemeColorChoice) = darkColorScheme(
    primary = choice.primaryColor,
    onPrimary = Navy900,
    secondary = choice.secondaryColor,
    onSecondary = Navy900,
    tertiary = Coral500,
    background = Navy900,
    onBackground = TextPrimaryDark,
    surface = Navy800,
    onSurface = TextPrimaryDark,
    surfaceVariant = Navy700,
    onSurfaceVariant = TextSecondaryDark
)

fun getDynamicLightColorScheme(choice: ThemeColorChoice) = lightColorScheme(
    primary = choice.primaryColor,
    onPrimary = Color.White,
    secondary = choice.secondaryColor,
    onSecondary = Color.White,
    tertiary = Coral500,
    background = Color(0xFFF1F5F9),
    onBackground = Color(0xFF0F172A),
    surface = Color.White,
    onSurface = Color(0xFF0F172A),
    surfaceVariant = Color(0xFFE2E8F0),
    onSurfaceVariant = Color(0xFF475569)
)

@Composable
fun MyApplicationTheme(
    appSettings: AppSettings,
    content: @Composable () -> Unit,
) {
    val colorScheme = if (appSettings.isDarkMode) {
        getDynamicDarkColorScheme(appSettings.themeColor)
    } else {
        getDynamicLightColorScheme(appSettings.themeColor)
    }

    val appColors = getAppColors(appSettings)

    CompositionLocalProvider(
        LocalAppColors provides appColors,
        LocalFontScale provides appSettings.fontScale
    ) {
        MaterialTheme(colorScheme = colorScheme, typography = Typography, content = content)
    }
}
