package id.explorstreet.app.ui.screens

import android.Manifest
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.ReportProblem
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import id.explorstreet.app.AppViewModel
import id.explorstreet.app.UiState
import id.explorstreet.app.data.Stored
import id.explorstreet.app.location.hasLocationPermission
import id.explorstreet.app.location.rememberLiveLocation
import id.explorstreet.app.ui.components.ActionButton
import id.explorstreet.app.ui.components.ActionStyle
import id.explorstreet.app.ui.components.AppCard
import id.explorstreet.app.ui.components.NoteBox
import id.explorstreet.app.ui.components.OsmMap
import id.explorstreet.app.ui.components.Pill
import id.explorstreet.app.ui.components.RoutePreview
import id.explorstreet.app.ui.components.SectionHeader
import id.explorstreet.app.ui.components.StatBox
import id.explorstreet.app.ui.formatTime
import id.explorstreet.app.ui.theme.AppTheme

private val reportTypes = listOf("Trotoar terhalang", "Permukaan jalan rusak", "Lalu lintas padat", "Pit-stop tidak tersedia")

@Composable
fun NavScreen(vm: AppViewModel, s: UiState, stored: Stored, onFinished: () -> Unit) {
    val context = LocalContext.current
    val route = vm.currentRoute(s)
    val progress = vm.progress(s)
    var granted by remember { mutableStateOf(hasLocationPermission(context)) }
    var showReport by rememberSaveable { mutableStateOf(false) }
    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { r -> granted = r.values.any { it } }
    val me by rememberLiveLocation(granted)

    ScreenColumn {
        SectionHeader("Live nav & reports", route.name, "Playback simulasi · peta © OpenStreetMap contributors")

        OsmMap(route.lat, route.lng, me)
        if (!granted) {
            ActionButton("Tampilkan posisiku di peta", {
                launcher.launch(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION))
            }, Modifier.fillMaxWidth(), ActionStyle.Outline, Icons.Default.MyLocation)
        } else {
            Text(
                me?.let { "Posisimu: %.5f, %.5f · tidak disimpan".format(it.latitude, it.longitude) } ?: "Mencari sinyal GPS…",
                style = MaterialTheme.typography.bodySmall, color = AppTheme.extra.muted,
            )
        }

        AppCard {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    when { progress == 100 -> "Selesai"; s.playing -> "Simulasi berjalan"; s.elapsed > 0 -> "Dijeda"; else -> "Belum dimulai" },
                    style = MaterialTheme.typography.titleSmall, modifier = Modifier.weight(1f),
                )
                Pill("$progress%")
            }
            LinearProgressIndicator(
                progress = { progress / 100f },
                modifier = Modifier.fillMaxWidth().height(8.dp).clip(RoundedCornerShape(99.dp)),
                trackColor = MaterialTheme.colorScheme.surfaceVariant,
            )
            RoutePreview(route.path, route.start, dot = Offset(48f + s.navStep * 75f, 262f - s.navStep * 51f))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                StatBox(formatTime(s.elapsed), "Waktu simulasi", Modifier.weight(1f))
                StatBox("${s.navStep + 1}/${vm.navSteps.size}", "Tahap", Modifier.weight(1f))
            }
            Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Text(vm.navSteps[s.navStep][0], style = MaterialTheme.typography.titleMedium)
                Text(vm.navSteps[s.navStep][1], style = MaterialTheme.typography.bodySmall, color = AppTheme.extra.muted)
            }
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                ActionButton(
                    if (s.playing) "Jeda" else if (s.elapsed > 0) "Lanjut" else "Mulai",
                    vm::togglePlay, Modifier.weight(1f), icon = if (s.playing) Icons.Default.Pause else Icons.Default.PlayArrow,
                )
                ActionButton("Tahap", vm::nextStep, Modifier.weight(1f), ActionStyle.Secondary, Icons.Default.SkipNext, enabled = s.navStep < vm.lastStep)
            }
            ActionButton("Selesai & buat rekap", { if (vm.finishNav()) onFinished() }, Modifier.fillMaxWidth(), ActionStyle.Lime, Icons.Default.Flag)
        }

        AppCard {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("Laporan kendala", style = MaterialTheme.typography.titleMedium, modifier = Modifier.weight(1f))
                TextButton({ showReport = true }) { Text("+ Lapor") }
            }
            if (stored.reports.isEmpty()) NoteBox("Belum ada laporan yang dibuat dalam demo ini.")
            stored.reports.forEach { r ->
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Column(Modifier.weight(1f)) {
                        Text(r.type, style = MaterialTheme.typography.titleSmall)
                        Text(r.note.ifBlank { "Tidak ada catatan tambahan" }, style = MaterialTheme.typography.bodySmall, color = AppTheme.extra.muted)
                    }
                    Pill("Draf lokal", warm = true)
                }
            }
        }
    }

    if (showReport) ReportDialog(onDismiss = { showReport = false }) { type, note -> vm.addReport(type, note); showReport = false }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ReportDialog(onDismiss: () -> Unit, onSave: (String, String) -> Unit) {
    var type by rememberSaveable { mutableStateOf(reportTypes.first()) }
    var note by rememberSaveable { mutableStateOf("") }
    var expanded by remember { mutableStateOf(false) }
    AlertDialog(
        onDismissRequest = onDismiss,
        icon = { androidx.compose.material3.Icon(Icons.Default.ReportProblem, null) },
        title = { Text("Catat kendala jalan") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text("Tidak dikirim ke publik. Hindari alamat rumah, sekolah, dan informasi pribadi.", style = MaterialTheme.typography.bodySmall)
                ExposedDropdownMenuBox(expanded, { expanded = it }) {
                    OutlinedTextField(type, {}, readOnly = true, label = { Text("Kategori kendala") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded) },
                        modifier = Modifier.menuAnchor(MenuAnchorType.PrimaryNotEditable).fillMaxWidth())
                    ExposedDropdownMenu(expanded, { expanded = false }) {
                        reportTypes.forEach { t -> DropdownMenuItem({ Text(t) }, { type = t; expanded = false }) }
                    }
                }
                OutlinedTextField(note, { note = it.take(160) }, label = { Text("Catatan opsional") }, modifier = Modifier.fillMaxWidth())
            }
        },
        confirmButton = { TextButton({ onSave(type, note) }) { Text("Simpan laporan") } },
        dismissButton = { TextButton(onDismiss) { Text("Batal") } },
    )
}
