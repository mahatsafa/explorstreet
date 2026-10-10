package id.explorstreet.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ConfirmationNumber
import androidx.compose.material.icons.filled.LocalCafe
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
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
import id.explorstreet.app.UiState
import id.explorstreet.app.data.Stored
import id.explorstreet.app.ui.components.ActionButton
import id.explorstreet.app.ui.components.ActionStyle
import id.explorstreet.app.ui.components.AppCard
import id.explorstreet.app.ui.components.NoteBox
import id.explorstreet.app.ui.components.Pill
import id.explorstreet.app.ui.components.SectionHeader
import id.explorstreet.app.ui.components.accentFor
import id.explorstreet.app.ui.money
import id.explorstreet.app.ui.theme.AppTheme

@Composable
fun StopsScreen(vm: AppViewModel, s: UiState, stored: Stored) {
    var voucher by rememberSaveable { mutableStateOf<Pair<String, String>?>(null) }
    val shown = vm.stops(s, stored)
    ScreenColumn {
        SectionHeader("Pit-stop & voucher", "Rehat sejenak, lanjut lagi", "Tempat singgah contoh setelah olahraga.")
        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            listOf("all" to "Semua", "budget" to "< 15 ribu", "bike" to "Ramah sepeda").forEach { (k, l) ->
                FilterChip(s.stopFilter == k, { vm.setStopFilter(k) }, { Text(l) })
            }
        }
        if (shown.isEmpty()) NoteBox("Belum ada tempat singgah contoh untuk filter ini.")
        shown.forEach { p ->
            AppCard {
                Box(
                    Modifier.fillMaxWidth().height(96.dp).clip(RoundedCornerShape(16.dp)).background(accentFor(p.color)),
                ) {
                    Icon(Icons.Default.LocalCafe, null, Modifier.align(Alignment.Center).size(34.dp), tint = MaterialTheme.colorScheme.onSurface)
                    Pill("Merchant fiktif", Modifier.align(Alignment.TopEnd).padding(10.dp), warm = true)
                }
                Row(verticalAlignment = Alignment.Top) {
                    Column(Modifier.weight(1f)) {
                        Text(p.name, style = MaterialTheme.typography.titleMedium)
                        Text(p.area, style = MaterialTheme.typography.bodySmall, color = AppTheme.extra.muted)
                    }
                    Pill(money(p.price), warm = true)
                }
                Text(p.perk, style = MaterialTheme.typography.bodySmall)
                HorizontalDivider(color = AppTheme.extra.line)
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(p.offer, style = MaterialTheme.typography.titleSmall, color = MaterialTheme.colorScheme.primary, modifier = Modifier.weight(1f))
                    ActionButton("Voucher demo", { vm.claimVoucher(); voucher = p.name to p.offer }, style = ActionStyle.Lime)
                }
            }
        }
    }
    voucher?.let { (name, offer) ->
        AlertDialog(
            onDismissRequest = { voucher = null },
            icon = { Icon(Icons.Default.ConfirmationNumber, null) },
            title = { Text(name) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(offer)
                    NoteBox("DEMO-ONLY · Tidak berlaku di kasir · Tidak dapat ditukar")
                }
            },
            confirmButton = { TextButton({ voucher = null }) { Text("Oke, mengerti") } },
        )
    }
}
