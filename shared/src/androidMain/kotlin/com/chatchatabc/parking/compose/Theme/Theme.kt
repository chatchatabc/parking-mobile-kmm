package com.chatchatabc.parking.compose.Theme

import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.chatchatabc.parkingadmin.R


private val LightColorScheme = lightColorScheme(
    primary = md_theme_light_primary,
    onPrimary = md_theme_light_onPrimary,
    primaryContainer = md_theme_light_primaryContainer,
    onPrimaryContainer = md_theme_light_onPrimaryContainer,
    secondary = md_theme_light_secondary,
    onSecondary = md_theme_light_onSecondary,
    secondaryContainer = md_theme_light_secondaryContainer,
    onSecondaryContainer = md_theme_light_onSecondaryContainer,
    tertiary = md_theme_light_tertiary,
    onTertiary = md_theme_light_onTertiary,
    tertiaryContainer = md_theme_light_tertiaryContainer,
    onTertiaryContainer = md_theme_light_onTertiaryContainer,
    error = md_theme_light_error,
    errorContainer = md_theme_light_errorContainer,
    onError = md_theme_light_onError,
    onErrorContainer = md_theme_light_onErrorContainer,
    background = md_theme_light_background,
    onBackground = md_theme_light_onBackground,
    outline = md_theme_light_outline,
    inverseOnSurface = md_theme_light_inverseOnSurface,
    inverseSurface = md_theme_light_inverseSurface,
    inversePrimary = md_theme_light_inversePrimary,
    surfaceTint = md_theme_light_surfaceTint,
    outlineVariant = md_theme_light_outlineVariant,
    scrim = md_theme_light_scrim,
    surface = md_theme_light_surface,
    onSurface = md_theme_light_onSurface,
    surfaceVariant = md_theme_light_surfaceVariant,
    onSurfaceVariant = md_theme_light_onSurfaceVariant,
)

val extended = ExtendedColorScheme(
    material = LightColorScheme,
    seedGreen = Green,
    seedOrange = Orange,
    seedYellow = Yellow,
    yellow = light_Yellow,
    onYellow = light_onYellow,
    yellowContainer = light_YellowContainer,
    onYellowContainer = light_onYellowContainer,
    green = light_Green,
    onGreen = light_onGreen,
    greenContainer = light_GreenContainer,
    onGreenContainer = light_onGreenContainer,
    orange = light_Orange,
    onOrange = light_onOrange,
    orangeContainer = light_OrangeContainer,
    onOrangeContainer = light_onOrangeContainer,
)


val poppins = FontFamily(
    Font(R.font.poppins_semibold, FontWeight.SemiBold),
)

private val LocalColors = staticCompositionLocalOf { extended }

@Composable
fun AppTheme(content: @Composable () -> Unit) {
    val colors = extended
    val typography = typography.copy(
        titleLarge = TextStyle(
            fontFamily = poppins,
            fontWeight = FontWeight.SemiBold,
            fontSize = 24.sp,
            letterSpacing = 0.15.sp,
            lineHeight = 32.sp,
        ),
        headlineLarge = TextStyle(
            fontFamily = poppins,
            fontWeight = FontWeight.SemiBold,
            fontSize = MaterialTheme.typography.headlineLarge.fontSize,
            letterSpacing = 0.15.sp,
            lineHeight = 32.sp,
        ),
    )

    CompositionLocalProvider(LocalColors provides colors) {
        MaterialTheme(
            colorScheme = colors.material,
            typography = typography,
            content = content,
        )
    }
}
data class ExtendedColorScheme(
    val seedYellow: Color,
    val seedOrange: Color,
    val seedGreen: Color,
    val yellow: Color,
    val onYellow: Color,
    val yellowContainer: Color,
    val onYellowContainer: Color,
    val green: Color,
    val onGreen: Color,
    val greenContainer: Color,
    val onGreenContainer: Color,
    val orange: Color,
    val onOrange: Color,
    val orangeContainer: Color,
    val onOrangeContainer: Color,
    val material: ColorScheme,
)

val MaterialTheme.extendedColors
    @Composable
    get() = LocalColors.current