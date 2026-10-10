package id.explorstreet.app.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.DirectionsBike
import androidx.compose.material.icons.automirrored.filled.DirectionsRun
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import id.explorstreet.app.ui.theme.AppTheme

val CardShape = RoundedCornerShape(23.dp)

@Composable
fun AppCard(modifier: Modifier = Modifier, padding: PaddingValues = PaddingValues(18.dp), content: @Composable ColumnScope.() -> Unit) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = CardShape,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, AppTheme.extra.line),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
    ) {
        Column(Modifier.padding(padding), verticalArrangement = Arrangement.spacedBy(12.dp), content = content)
    }
}

@Composable
fun Pill(text: String, modifier: Modifier = Modifier, warm: Boolean = false, icon: ImageVector? = null) {
    val ex = AppTheme.extra
    Row(
        modifier
            .clip(CircleShape)
            .background(if (warm) ex.warmBg else ex.pillBg)
            .padding(horizontal = 10.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(5.dp),
    ) {
        if (icon != null) Icon(icon, null, Modifier.size(12.dp), tint = if (warm) ex.warmFg else ex.pillFg)
        Text(text, style = MaterialTheme.typography.labelMedium, color = if (warm) ex.warmFg else ex.pillFg)
    }
}

@Composable
fun Eyebrow(text: String) {
    Text(text.uppercase(), style = MaterialTheme.typography.labelSmall, color = AppTheme.extra.muted)
}

enum class ActionStyle { Primary, Lime, Secondary, Outline }

@Composable
fun ActionButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    style: ActionStyle = ActionStyle.Primary,
    icon: ImageVector? = null,
    enabled: Boolean = true,
) {
    val shape = RoundedCornerShape(14.dp)
    val content: @Composable RowScope.() -> Unit = {
        if (icon != null) { Icon(icon, null, Modifier.size(18.dp)); Spacer(Modifier.width(8.dp)) }
        Text(text, style = MaterialTheme.typography.labelLarge)
    }
    val m = modifier.heightIn(min = 46.dp)
    val cs = MaterialTheme.colorScheme
    when (style) {
        ActionStyle.Outline -> OutlinedButton(onClick, m, enabled, shape, border = BorderStroke(1.dp, cs.outline), content = content)
        else -> {
            val (bg, fg) = when (style) {
                ActionStyle.Primary -> cs.primary to cs.onPrimary
                ActionStyle.Lime -> cs.secondary to cs.onSecondary
                else -> cs.secondaryContainer to cs.onSecondaryContainer
            }
            Button(onClick, m, enabled, shape, ButtonDefaults.buttonColors(containerColor = bg, contentColor = fg), content = content)
        }
    }
}

@Composable
fun NoteBox(text: String, modifier: Modifier = Modifier) {
    Text(
        text,
        modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .padding(12.dp),
        style = MaterialTheme.typography.bodySmall,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
    )
}

@Composable
fun accentFor(color: String): Color = when (color) {
    "gold" -> AppTheme.extra.accentGold
    "blue" -> AppTheme.extra.accentBlue
    else -> AppTheme.extra.accentGreen
}

fun sportIcon(sport: String) = if (sport == "run") Icons.AutoMirrored.Filled.DirectionsRun else Icons.AutoMirrored.Filled.DirectionsBike

@Composable
fun IconTile(icon: ImageVector, color: String, size: Int = 46) {
    Box(
        Modifier.size(size.dp).clip(RoundedCornerShape(14.dp)).background(accentFor(color)),
        contentAlignment = Alignment.Center,
    ) { Icon(icon, null, tint = MaterialTheme.colorScheme.onSurface) }
}

@Composable
fun SectionHeader(eyebrow: String, title: String, subtitle: String? = null, trailing: @Composable (() -> Unit)? = null) {
    Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.Bottom) {
        Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Eyebrow(eyebrow)
            Text(title, style = MaterialTheme.typography.headlineMedium)
            if (subtitle != null) Text(subtitle, style = MaterialTheme.typography.bodySmall, color = AppTheme.extra.muted)
        }
        trailing?.invoke()
    }
}

@Composable
fun StatBox(value: String, label: String, modifier: Modifier = Modifier) {
    Column(
        modifier
            .clip(RoundedCornerShape(16.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .border(1.dp, AppTheme.extra.line, RoundedCornerShape(16.dp))
            .padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(2.dp),
    ) {
        Text(value, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.ExtraBold)
        Text(label, style = MaterialTheme.typography.bodySmall, color = AppTheme.extra.muted)
    }
}
