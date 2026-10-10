# explorstreet

Aplikasi Android untuk eksplorasi rute lari & gowes di Malang, dengan pit-stop, teman olahraga, dan dashboard mitra. Dibangun dengan **Kotlin + Jetpack Compose**. Saat ini semua data masih contoh/simulasi dan disimpan lokal di perangkat.

## Fitur (6 layar)
| Layar | Isi |
|---|---|
| Rute | Vibe Route Generator: pilih lari/gowes, target jarak, slider suasana; rute diurutkan berdasarkan skor |
| Navigasi | Peta OpenStreetMap + posisi GPS (opsional), playback simulasi langkah, laporan kendala jalan |
| Buddy | Agenda kelompok contoh dengan filter olahraga & ritme, tandai minat |
| Pit-stop | Tempat singgah contoh, filter harga/ramah sepeda, voucher demo |
| Feed | Rekap sesi simulasi, catatan pribadi, apresiasi |
| Mitra (ikon toko di kanan atas) | Statistik interaksi lokal, buat promo demo yang muncul di Pit-stop |

Tema terang/gelap otomatis mengikuti setelan sistem.

## Semua gratis
- Android Studio, Kotlin, AndroidX/Jetpack — gratis
- Peta: MapLibre + tile OpenStreetMap (tanpa API key). Wajib tetap menampilkan atribusi "© OpenStreetMap contributors". Untuk produksi dengan banyak pengguna, ganti ke penyedia tile yang mengizinkan (mis. self-host atau free tier) sesuai [kebijakan tile OSM](https://operations.osmfoundation.org/policies/tiles/).
- Font Outfit & DM Sans — lisensi OFL (Google Fonts)
- Ikon — Material Icons (Apache 2.0)
- Distribusi — bagikan file APK langsung (tanpa Play Store)

## Cara build

### A. Android Studio (paling mudah, di Windows)
1. Install [Android Studio](https://developer.android.com/studio).
2. File → Open → pilih folder `explorstreet` (di Windows: `D:\Aaa\code\explorstreet`).
3. Tunggu Gradle sync selesai (pertama kali unduh dependensi).
4. Pilih emulator/HP lalu klik ▶ Run.
5. APK: Build → Build App Bundle(s) / APK(s) → Build APK(s) → `app/build/outputs/apk/debug/app-debug.apk`.

### B. Command line
```bash
./gradlew assembleDebug
# hasil: app/build/outputs/apk/debug/app-debug.apk
```
Butuh JDK 17+ dan Android SDK (platform 35) dengan `ANDROID_HOME` atau `local.properties` berisi `sdk.dir=...`.

### APK rilis bertanda tangan (untuk dibagikan)
```bash
keytool -genkeypair -v -keystore explorstreet.jks -alias explorstreet -keyalg RSA -keysize 2048 -validity 10000
```
Buat `keystore.properties` di root (jangan di-commit):
```
storeFile=explorstreet.jks
storePassword=...
keyAlias=explorstreet
keyPassword=...
```
Lalu `./gradlew assembleRelease` → `app/build/outputs/apk/release/app-release.apk`. Simpan keystore baik-baik; tanpa itu update APK tidak bisa dipasang di atas versi lama.

## Struktur
```
app/src/main/
  assets/seed.json            data contoh (rute, agenda, pit-stop, feed, tahap navigasi)
  java/id/explorstreet/app/
    MainActivity.kt           scaffold, top bar, bottom navigation
    AppViewModel.kt           semua logika (skor rute, simulasi navigasi, voucher, feed, promo)
    data/                     model + Repository (DataStore lokal; siap diganti Supabase/Firebase)
    location/                 GPS (FusedLocationProvider), hanya saat layar Navigasi terbuka
    ui/theme/                 warna, font, tema terang/gelap
    ui/components/            kartu, pill, tombol, peta OSM, pratinjau rute
    ui/screens/               6 layar
```

## Langkah berikutnya
- Ganti data contoh dengan rute & merchant asli yang sudah divalidasi lapangan.
- Garis rute asli di peta (GeoJSON) + routing gratis (OSRM / GraphHopper free tier).
- Backend gratis (Supabase/Firebase free tier) untuk akun, feed, dan buddy sungguhan.
