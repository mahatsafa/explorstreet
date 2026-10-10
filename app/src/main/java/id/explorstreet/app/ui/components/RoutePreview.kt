package id.explorstreet.app.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.graphics.vector.PathParser
import androidx.compose.material3.MaterialTheme
import id.explorstreet.app.ui.theme.AppTheme

/** Ilustrasi rute dari path SVG prototipe (viewBox 0 0 460 360). */
@Composable
fun RoutePreview(path: String, start: List<Float>, modifier: Modifier = Modifier, dot: Offset? = null) {
    val parsed = remember(path) { PathParser().parsePathString(path).toPath() }
    val bg = AppTheme.extra.mapBg
    val grid = AppTheme.extra.line
    val route = MaterialTheme.colorScheme.primary
    val halo = MaterialTheme.colorScheme.secondary
    Canvas(modifier.fillMaxWidth().aspectRatio(460f / 360f).clip(CardShape)) {
        drawRect(bg)
        val s = size.width / 460f
        // jalan-jalan dekoratif
        for (i in 1..5) {
            drawLine(grid, Offset(0f, size.height * i / 6f), Offset(size.width, size.height * i / 6f + 30f * s), 6f * s)
            drawLine(grid, Offset(size.width * i / 6f, 0f), Offset(size.width * i / 6f - 25f * s, size.height), 6f * s)
        }
        withTransform({ scale(s, s, Offset.Zero) }) {
            drawPath(parsed, halo.copy(alpha = 0.7f), style = Stroke(width = 18f, cap = StrokeCap.Round))
            drawPath(parsed, route, style = Stroke(width = 6f, cap = StrokeCap.Round))
            drawCircle(Color.White, 11f, Offset(start[0], start[1]))
            drawCircle(route, 7f, Offset(start[0], start[1]))
            if (dot != null) {
                drawCircle(halo, 16f, dot)
                drawCircle(route, 9f, dot)
            }
        }
    }
}
