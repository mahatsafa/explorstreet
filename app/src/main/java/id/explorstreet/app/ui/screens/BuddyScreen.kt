package id.explorstreet.app.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import id.explorstreet.app.AppViewModel
import id.explorstreet.app.UiState
import id.explorstreet.app.data.Stored
import id.explorstreet.app.ui.components.ActionButton
import id.explorstreet.app.ui.components.ActionStyle
import id.explorstreet.app.ui.components.AppCard
import id.explorstreet.app.ui.components.Eyebrow
import id.explorstreet.app.ui.components.NoteBox
import id.explorstreet.app.ui.components.Pill
import id.explorstreet.app.ui.components.SectionHeader
import id.explorstreet.app.ui.theme.AppTheme

@Composable
fun BuddyScreen(vm: AppViewModel, s: UiState, stored: Stored) {
    val list = vm.events(s)
    ScreenColumn {
        SectionHeader("Pace buddy match", "Gerak bareng lebih seru", "Agenda kelompok contoh di ruang publik.") { Pill("${list.size} contoh") }
        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            listOf("all" to "Semua", "run" to "Lari", "bike" to "Gowes").forEach { (k, l) ->
                FilterChip(s.buddySport == k, { vm.setBuddySport(k) }, { Text(l) })
            }
        }
        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            listOf("all" to "Semua ritme", "easy" to "Santai", "steady" to "Stabil").forEach { (k, l) ->
                FilterChip(s.buddyStyle == k, { vm.setBuddyStyle(k) }, { Text(l) })
            }
        }
        if (list.isEmpty()) NoteBox("Tidak ada kegiatan contoh untuk filter tersebut.")
        list.forEach { e ->
            val interested = e.id in stored.eventInterest
            AppCard {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Pill("${if (e.sport == "run") "Lari" else "Gowes"} · ${if (e.style == "easy") "Santai" else "Stabil"}")
                    Row(Modifier.weight(1f)) {}
                    Eyebrow("Agenda fiktif")
                }
                Text(e.title, style = MaterialTheme.typography.titleLarge)
                Text(e.org, style = MaterialTheme.typography.bodySmall, color = AppTheme.extra.muted)
                Meta(Icons.Default.Schedule, e.`when`)
                Meta(Icons.Default.Place, e.area)
                NoteBox("${e.range} · Kegiatan tidak tersedia untuk pendaftaran asli.")
                ActionButton(
                    if (interested) "Tandai tidak tertarik" else "Tandai tertarik (demo)",
                    { vm.toggleInterest(e.id) }, Modifier.fillMaxWidth(),
                    if (interested) ActionStyle.Secondary else ActionStyle.Primary,
                )
            }
        }
    }
}

@Composable
private fun Meta(icon: ImageVector, text: String) {
    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
        Icon(icon, null, Modifier.size(15.dp), tint = AppTheme.extra.muted)
        Text(text, style = MaterialTheme.typography.bodySmall)
    }
}
