package id.explorstreet.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import id.explorstreet.app.AppViewModel
import id.explorstreet.app.data.Stored
import id.explorstreet.app.ui.components.ActionButton
import id.explorstreet.app.ui.components.AppCard
import id.explorstreet.app.ui.components.NoteBox
import id.explorstreet.app.ui.components.Pill
import id.explorstreet.app.ui.components.SectionHeader
import id.explorstreet.app.ui.components.StatBox

@Composable
fun PartnerScreen(vm: AppViewModel, stored: Stored, onPromoCreated: () -> Unit) {
    var merchant by rememberSaveable { mutableStateOf("") }
    var offer by rememberSaveable { mutableStateOf("") }
    var price by rememberSaveable { mutableStateOf("") }
    ScreenColumn {
        SectionHeader("Partner B2B · simulasi", "Jadi titik singgah pilihan",
            "Statistik berasal dari interaksi di perangkat ini — bukan pengunjung, omzet, atau transaksi.")

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            StatBox("${stored.routePreviewClicks}", "Pratinjau rute", Modifier.weight(1f))
            StatBox("${stored.voucherInterest}", "Minat voucher", Modifier.weight(1f))
        }
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            StatBox("${stored.eventInterest.size}", "Minat agenda", Modifier.weight(1f))
            StatBox("${stored.reports.size}", "Laporan contoh", Modifier.weight(1f))
        }

        AppCard {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("Buat promo demo", style = MaterialTheme.typography.titleMedium, modifier = Modifier.weight(1f))
                Pill("Lokal")
            }
            OutlinedTextField(merchant, { merchant = it.take(45) }, Modifier.fillMaxWidth(), label = { Text("Nama merchant simulasi") }, singleLine = true)
            OutlinedTextField(offer, { offer = it.take(58) }, Modifier.fillMaxWidth(), label = { Text("Judul promosi contoh") }, singleLine = true)
            OutlinedTextField(price, { price = it.filter(Char::isDigit).take(7) }, Modifier.fillMaxWidth(), label = { Text("Harga produk contoh (Rp)") },
                singleLine = true, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number))
            ActionButton("Buat kartu promo contoh", {
                if (vm.addPromo(merchant, offer, price)) { merchant = ""; offer = ""; price = ""; onPromoCreated() }
            }, Modifier.fillMaxWidth())
            NoteBox("Kartu promo akan muncul di layar Pit-stop. Tidak diterbitkan ke pelanggan sungguhan.")
        }

        AppCard {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("Ilustrasi tren", style = MaterialTheme.typography.titleMedium, modifier = Modifier.weight(1f))
                Pill("Angka fiktif", warm = true)
            }
            val bars = listOf(0.35f, 0.5f, 0.42f, 0.66f, 0.58f, 0.9f)
            Row(Modifier.fillMaxWidth().height(120.dp), horizontalArrangement = Arrangement.spacedBy(10.dp), verticalAlignment = Alignment.Bottom) {
                bars.forEach { h ->
                    Box(Modifier.weight(1f).fillMaxHeight(h).clip(RoundedCornerShape(8.dp)).background(MaterialTheme.colorScheme.primary))
                }
            }
            Row(Modifier.fillMaxWidth()) {
                listOf("Sen", "Sel", "Rab", "Kam", "Jum", "Sab").forEach {
                    Text(it, Modifier.weight(1f), style = MaterialTheme.typography.bodySmall, textAlign = androidx.compose.ui.text.style.TextAlign.Center)
                }
            }
        }

        AppCard {
            Text("Model pendapatan · hipotesis", style = MaterialTheme.typography.titleMedium)
            listOf(
                "Penempatan sponsor transparan" to "Merchant membayar promosi, tanpa mengubah prioritas keselamatan atau memalsukan kualitas rute.",
                "Pengukuran voucher terverifikasi" to "Baru mungkin jika merchant menyetujui integrasi dan mekanisme penukaran valid.",
                "Tanpa profil GPS pelanggan" to "Dashboard tidak mengumpulkan rute rumah, sekolah, atau lokasi langsung pengguna.",
            ).forEach { (t, d) ->
                Column {
                    Text(t, style = MaterialTheme.typography.titleSmall)
                    Text(d, style = MaterialTheme.typography.bodySmall)
                }
            }
        }
    }
}
