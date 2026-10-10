package id.explorstreet.app.ui

import java.text.NumberFormat
import java.util.Locale

private val id = Locale("id", "ID")

fun formatKm(x: Double): String = NumberFormat.getNumberInstance(id).apply { maximumFractionDigits = 1 }.format(x)
fun money(x: Int): String = NumberFormat.getCurrencyInstance(id).apply { maximumFractionDigits = 0 }.format(x)
fun formatTime(seconds: Int): String = "%02d:%02d".format(seconds / 60, seconds % 60)
fun levelText(n: Int): String = listOf("Tidak penting", "Boleh ada", "Cukup penting", "Prioritas tinggi")[n]
