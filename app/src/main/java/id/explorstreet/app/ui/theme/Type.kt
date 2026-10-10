package id.explorstreet.app.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.ExperimentalTextApi
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontVariation
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import id.explorstreet.app.R

@OptIn(ExperimentalTextApi::class)
private fun variable(res: Int, weight: FontWeight) =
    Font(res, weight, variationSettings = FontVariation.Settings(FontVariation.weight(weight.weight)))

val Outfit = FontFamily(
    listOf(FontWeight.Medium, FontWeight.SemiBold, FontWeight.Bold, FontWeight.ExtraBold).map { variable(R.font.outfit, it) }
)
val DmSans = FontFamily(
    listOf(FontWeight.Normal, FontWeight.Medium, FontWeight.SemiBold, FontWeight.Bold).map { variable(R.font.dm_sans, it) }
)

private val base = Typography()

val AppTypography = Typography(
    headlineLarge = TextStyle(fontFamily = Outfit, fontWeight = FontWeight.ExtraBold, fontSize = 30.sp, lineHeight = 34.sp, letterSpacing = (-0.5).sp),
    headlineMedium = TextStyle(fontFamily = Outfit, fontWeight = FontWeight.ExtraBold, fontSize = 24.sp, lineHeight = 28.sp),
    titleLarge = TextStyle(fontFamily = Outfit, fontWeight = FontWeight.ExtraBold, fontSize = 19.sp, lineHeight = 24.sp),
    titleMedium = TextStyle(fontFamily = Outfit, fontWeight = FontWeight.ExtraBold, fontSize = 16.sp, lineHeight = 21.sp),
    titleSmall = TextStyle(fontFamily = Outfit, fontWeight = FontWeight.Bold, fontSize = 13.sp, lineHeight = 18.sp),
    bodyLarge = base.bodyLarge.copy(fontFamily = DmSans, fontSize = 15.sp),
    bodyMedium = base.bodyMedium.copy(fontFamily = DmSans, fontSize = 14.sp, lineHeight = 21.sp),
    bodySmall = base.bodySmall.copy(fontFamily = DmSans, fontSize = 12.sp, lineHeight = 17.sp),
    labelLarge = TextStyle(fontFamily = Outfit, fontWeight = FontWeight.ExtraBold, fontSize = 13.sp),
    labelMedium = TextStyle(fontFamily = Outfit, fontWeight = FontWeight.ExtraBold, fontSize = 11.sp),
    labelSmall = TextStyle(fontFamily = Outfit, fontWeight = FontWeight.ExtraBold, fontSize = 10.sp, letterSpacing = 1.2.sp),
)
