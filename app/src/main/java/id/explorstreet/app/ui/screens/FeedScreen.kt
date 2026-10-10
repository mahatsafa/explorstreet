package id.explorstreet.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import id.explorstreet.app.AppViewModel
import id.explorstreet.app.data.Stored
import id.explorstreet.app.ui.components.ActionButton
import id.explorstreet.app.ui.components.AppCard
import id.explorstreet.app.ui.components.Pill
import id.explorstreet.app.ui.components.SectionHeader
import id.explorstreet.app.ui.components.StatBox
import id.explorstreet.app.ui.formatKm
import id.explorstreet.app.ui.formatTime
import id.explorstreet.app.ui.theme.AppTheme

@Composable
fun FeedScreen(vm: AppViewModel, stored: Stored) {
    var draft by rememberSaveable { mutableStateOf("") }
    val recap = stored.recap
    val route = recap?.let { r -> vm.seed.routes.find { it.id == r.routeId } }
    ScreenColumn {
        SectionHeader("Recap & social feed", "Cerita setelah bergerak")

        AppCard {
            Text(route?.name ?: "Belum ada sesi simulasi.", style = MaterialTheme.typography.titleLarge)
            Text(
                if (route != null) "Rekap otomatis dari playback navigasi, bukan aktivitas fisik."
                else "Coba jalankan navigasi demo terlebih dahulu.",
                style = MaterialTheme.typography.bodySmall, color = AppTheme.extra.muted,
            )
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                StatBox(if (route != null) "${formatKm(route.km * recap.progress / 100)} km" else "—", "Jarak", Modifier.weight(1f))
                StatBox(if (recap != null && route != null) "${recap.progress}%" else "—", "Progres", Modifier.weight(1f))
                StatBox(if (recap != null && route != null) formatTime(recap.seconds) else "—", "Durasi", Modifier.weight(1f))
            }
        }

        AppCard {
            Text("Tulis catatan", style = MaterialTheme.typography.titleMedium)
            OutlinedTextField(draft, { draft = it.take(180) }, Modifier.fillMaxWidth(), placeholder = { Text("Gimana sesi hari ini?") }, minLines = 2)
            ActionButton("Simpan catatan", { if (vm.savePost(draft)) draft = "" }, Modifier.fillMaxWidth())
        }

        vm.feed(stored).forEach { post ->
            val liked = post.id in stored.kudos
            AppCard {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    Box(Modifier.size(38.dp).clip(CircleShape).background(MaterialTheme.colorScheme.secondary), contentAlignment = Alignment.Center) {
                        Text(post.initial, style = MaterialTheme.typography.titleSmall, color = MaterialTheme.colorScheme.onSecondary)
                    }
                    Column(Modifier.weight(1f)) {
                        Text(post.author, style = MaterialTheme.typography.titleSmall)
                        Text(post.time, style = MaterialTheme.typography.bodySmall, color = AppTheme.extra.muted)
                    }
                    Pill("Contoh")
                }
                Text(post.text, style = MaterialTheme.typography.bodyMedium)
                HorizontalDivider(color = AppTheme.extra.line)
                TextButton({ vm.toggleKudos(post.id) }) {
                    Icon(if (liked) Icons.Default.Favorite else Icons.Default.FavoriteBorder, null, Modifier.size(18.dp))
                    Text(if (liked) "  Diberi apresiasi · demo" else "  Apresiasi · demo")
                }
            }
        }
    }
}
