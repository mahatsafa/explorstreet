package id.explorstreet.app.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

/** Warna tambahan di luar Material: aksen kartu rute & ilustrasi. */
@Immutable
data class ExtraColors(
    val muted: Color,
    val line: Color,
    val accentGreen: Color,
    val accentGold: Color,
    val accentBlue: Color,
    val pillBg: Color,
    val pillFg: Color,
    val warmBg: Color,
    val warmFg: Color,
    val mapBg: Color,
)

private val LightExtra = ExtraColors(
    muted = Muted, line = Line,
    accentGreen = Color(0xFFDCEFD6), accentGold = Color(0xFFFFE8C2), accentBlue = Color(0xFFDDEFF8),
    pillBg = Color(0xFFE9F6E4), pillFg = Color(0xFF356E48),
    warmBg = Color(0xFFFFF1DC), warmFg = Color(0xFF996623),
    mapBg = Color(0xFFE6F0E1),
)
private val DarkExtra = ExtraColors(
    muted = Slate400, line = Color(0x14FFFFFF),
    accentGreen = Color(0xFF064E3B), accentGold = Color(0xFF713F12), accentBlue = Color(0xFF075985),
    pillBg = Color(0x2610B981), pillFg = Emerald300,
    warmBg = Color(0x26FDE047), warmFg = Color(0xFFFEF08A),
    mapBg = Slate900,
)

val LocalExtraColors = staticCompositionLocalOf { LightExtra }

private val LightScheme = lightColorScheme(
    primary = Forest, onPrimary = Color.White,
    secondary = Lime, onSecondary = Ink,
    secondaryContainer = Soft, onSecondaryContainer = Forest,
    tertiary = Orange,
    background = Cream, onBackground = Ink,
    surface = Paper, onSurface = Ink,
    surfaceVariant = Soft, onSurfaceVariant = Muted,
    outline = Line, outlineVariant = Line,
    error = Color(0xFFB4423A),
)
private val DarkScheme = darkColorScheme(
    primary = Emerald500, onPrimary = Slate950,
    secondary = Lime, onSecondary = Slate950,
    secondaryContainer = Slate800, onSecondaryContainer = Emerald300,
    tertiary = Orange,
    background = Slate950, onBackground = Slate100,
    surface = Slate900, onSurface = Slate100,
    surfaceVariant = Slate800, onSurfaceVariant = Slate400,
    outline = Slate700, outlineVariant = Slate800,
)

object AppTheme {
    val extra: ExtraColors @Composable get() = LocalExtraColors.current
}

@Composable
fun ExplorstreetTheme(dark: Boolean = isSystemInDarkTheme(), content: @Composable () -> Unit) {
    androidx.compose.runtime.CompositionLocalProvider(LocalExtraColors provides if (dark) DarkExtra else LightExtra) {
        MaterialTheme(
            colorScheme = if (dark) DarkScheme else LightScheme,
            typography = AppTypography,
            content = content,
        )
    }
}
