package id.explorstreet.app.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Navigation
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import id.explorstreet.app.AppViewModel
import id.explorstreet.app.Target
import id.explorstreet.app.UiState
import id.explorstreet.app.ui.components.ActionButton
import id.explorstreet.app.ui.components.AppCard
import id.explorstreet.app.ui.components.IconTile
import id.explorstreet.app.ui.components.Pill
import id.explorstreet.app.ui.components.RoutePreview
import id.explorstreet.app.ui.components.SectionHeader
import id.explorstreet.app.ui.components.StatBox
import id.explorstreet.app.ui.components.sportIcon
import id.explorstreet.app.ui.formatKm
import id.explorstreet.app.ui.levelText
import id.explorstreet.app.ui.theme.AppTheme

@Composable
fun RouteScreen(vm: AppViewModel, s: UiState, onStartNav: () -> Unit) {
    var showInfo by rememberSaveable { mutableStateOf(false) }
    val route = vm.currentRoute(s)
    val candidates = vm.candidates(s)

    ScreenColumn {
        SectionHeader("Vibe route generator", "Mau gerak ke mana hari ini?", "Pilih suasana, explorstreet urutkan rute contoh di Malang.") {
            IconButton({ showInfo = true }) { Icon(Icons.Default.Info, "Info data") }
        }

        AppCard {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                listOf("run" to "Lari", "bike" to "Gowes").forEach { (k, label) ->
                    FilterChip(s.sport == k, { vm.setSport(k) }, { Text(label) }, leadingIcon = { Icon(sportIcon(k), null) })
                }
            }
            Text("Target jarak", style = MaterialTheme.typography.titleSmall)
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                listOf(Target.Short to "2–4 km", Target.Medium to "4–7 km", Target.Long to ">7 km", Target.All to "Semua").forEach { (t, l) ->
                    FilterChip(s.target == t, { vm.setTarget(t) }, { Text(l, style = MaterialTheme.typography.labelMedium) })
                }
            }
            VibeSlider("Suasana tenang", s.quiet, vm::setQuiet)
            VibeSlider("Jalur datar", s.flat, vm::setFlat)
            VibeSlider("Dekat kopi", s.coffee, vm::setCoffee)
        }

        AppCard {
            RoutePreview(route.path, route.start)
            Pill("Contoh · ${route.area}")
            Text(route.name, style = MaterialTheme.typography.titleLarge)
            Text("Area ${route.area} · ${if (route.sport == "run") "Lari" else "Gowes"} · Data simulasi",
                style = MaterialTheme.typography.bodySmall, color = AppTheme.extra.muted)
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                StatBox("${formatKm(route.km)} km", "Jarak", Modifier.weight(1f))
                StatBox("${route.min} mnt", "Durasi", Modifier.weight(1f))
                StatBox("${route.climb} m", "Naik", Modifier.weight(1f))
            }
            Text(route.description, style = MaterialTheme.typography.bodyMedium)
            ActionButton("Mulai navigasi demo", { vm.resetSimulation(); onStartNav() }, Modifier.fillMaxWidth(), icon = Icons.Default.Navigation)
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
            Text("Rute yang cocok", style = MaterialTheme.typography.titleMedium, modifier = Modifier.weight(1f))
            Pill("${candidates.size} pilihan")
        }
        candidates.forEach { r ->
            val selected = r.id == s.routeId
            Surface(
                Modifier.fillMaxWidth().clickable { vm.selectRoute(r.id) },
                shape = RoundedCornerShape(20.dp),
                color = MaterialTheme.colorScheme.surface,
                border = BorderStroke(if (selected) 2.dp else 1.dp, if (selected) MaterialTheme.colorScheme.primary else AppTheme.extra.line),
            ) {
                Row(Modifier.padding(14.dp), horizontalArrangement = Arrangement.spacedBy(12.dp), verticalAlignment = Alignment.CenterVertically) {
                    IconTile(sportIcon(r.sport), r.color)
                    Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(3.dp)) {
                        Text(r.name, style = MaterialTheme.typography.titleSmall)
                        Text("${formatKm(r.km)} km · naik ${r.climb} m · ${r.min} mnt", style = MaterialTheme.typography.bodySmall, color = AppTheme.extra.muted)
                    }
                    if (selected) Icon(Icons.Default.CheckCircle, null, tint = MaterialTheme.colorScheme.primary)
                }
            }
        }
    }

    if (showInfo) AlertDialog(
        onDismissRequest = { showInfo = false },
        confirmButton = { TextButton({ showInfo = false }) { Text("Saya mengerti") } },
        title = { Text("Prototipe ≠ navigasi nyata") },
        text = {
            Text("explorstreet menampilkan rute, elevasi, durasi, tempat singgah, kegiatan, voucher, komunitas, dan dashboard merchant menggunakan contoh fiktif. Peta memakai OpenStreetMap; posisi GPS hanya ditampilkan di perangkat dan tidak dikirim ke mana pun. Validasi lapangan, data merchant, dan backend diperlukan sebelum operasional.")
        },
    )
}

@Composable
private fun VibeSlider(label: String, value: Int, onChange: (Int) -> Unit) {
    Column {
        Row {
            Text(label, style = MaterialTheme.typography.titleSmall, modifier = Modifier.weight(1f))
            Text(levelText(value), style = MaterialTheme.typography.bodySmall, color = AppTheme.extra.muted)
        }
        Slider(value.toFloat(), { onChange(it.toInt()) }, valueRange = 0f..3f, steps = 2)
    }
}
