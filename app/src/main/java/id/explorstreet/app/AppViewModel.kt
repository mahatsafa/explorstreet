package id.explorstreet.app

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import id.explorstreet.app.data.CustomPromo
import id.explorstreet.app.data.Event
import id.explorstreet.app.data.FeedPost
import id.explorstreet.app.data.Recap
import id.explorstreet.app.data.Report
import id.explorstreet.app.data.Repository
import id.explorstreet.app.data.Route
import id.explorstreet.app.data.Stop
import id.explorstreet.app.data.Stored
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.math.abs
import kotlin.math.roundToInt

enum class Target { All, Short, Medium, Long }

data class UiState(
    val sport: String = "run",
    val target: Target = Target.Medium,
    val quiet: Int = 3,
    val flat: Int = 2,
    val coffee: Int = 1,
    val routeId: String = "ijen-run",
    val stopFilter: String = "all",
    val buddySport: String = "all",
    val buddyStyle: String = "all",
    val navStep: Int = 0,
    val elapsed: Int = 0,
    val playing: Boolean = false,
)

class AppViewModel(private val repo: Repository) : ViewModel() {
    val seed = repo.seed
    val navSteps = seed.navSteps

    private val _ui = MutableStateFlow(UiState())
    val ui: StateFlow<UiState> = _ui.asStateFlow()

    val stored: StateFlow<Stored> = repo.stored.stateIn(viewModelScope, SharingStarted.Eagerly, Stored())

    private val _toasts = MutableSharedFlow<String>(extraBufferCapacity = 4)
    val toasts = _toasts.asSharedFlow()
    private fun notify(msg: String) { _toasts.tryEmit(msg) }

    private var navJob: Job? = null

    // ---- Rute ----
    private fun distanceFit(r: Route, t: Target): Double = when (t) {
        Target.All -> 0.0
        Target.Short -> if (r.km <= 4) 14.0 else -abs(r.km - 3) * 4
        Target.Medium -> if (r.km in 4.0..7.0) 14.0 else -abs(r.km - 5.5) * 4
        Target.Long -> if (r.km > 7) 14.0 else -abs(r.km - 9) * 4
    }

    fun routeScore(r: Route, s: UiState = _ui.value): Double =
        (r.quiet * s.quiet + r.flat * s.flat + r.coffee * s.coffee).toDouble() + distanceFit(r, s.target)

    fun candidates(s: UiState): List<Route> =
        seed.routes.filter { it.sport == s.sport }.sortedByDescending { routeScore(it, s) }

    fun currentRoute(s: UiState = _ui.value): Route = seed.routes.find { it.id == s.routeId } ?: seed.routes.first()

    private fun ensureRouteInSport() = _ui.update { s ->
        val c = candidates(s)
        if (c.none { it.id == s.routeId }) s.copy(routeId = c.first().id) else s
    }

    fun setSport(sport: String) { _ui.update { it.copy(sport = sport) }; ensureRouteInSport(); resetSimulation() }
    fun setTarget(t: Target) = _ui.update { it.copy(target = t) }
    fun setQuiet(v: Int) = _ui.update { it.copy(quiet = v) }
    fun setFlat(v: Int) = _ui.update { it.copy(flat = v) }
    fun setCoffee(v: Int) = _ui.update { it.copy(coffee = v) }

    fun selectRoute(id: String) {
        _ui.update { it.copy(routeId = id) }
        persist { it.copy(routePreviewClicks = it.routePreviewClicks + 1) }
    }

    // ---- Navigasi simulasi ----
    val lastStep get() = navSteps.size - 1
    fun progress(s: UiState) = (s.navStep.toDouble() / lastStep * 100).roundToInt()

    fun resetSimulation() {
        navJob?.cancel(); navJob = null
        _ui.update { it.copy(playing = false, navStep = 0, elapsed = 0) }
    }

    fun togglePlay() {
        if (_ui.value.navStep == lastStep) resetSimulation()
        if (_ui.value.playing) {
            navJob?.cancel(); navJob = null
            _ui.update { it.copy(playing = false) }
            return
        }
        _ui.update { it.copy(playing = true) }
        navJob = viewModelScope.launch {
            while (true) {
                delay(1000)
                _ui.update { s ->
                    val elapsed = s.elapsed + 1
                    val step = if (elapsed % 4 == 0 && s.navStep < lastStep) s.navStep + 1 else s.navStep
                    s.copy(elapsed = elapsed, navStep = step)
                }
                if (_ui.value.navStep == lastStep) {
                    _ui.update { it.copy(playing = false) }
                    notify("Simulasi sampai titik akhir.")
                    break
                }
            }
        }
    }

    fun nextStep() {
        _ui.update { if (it.navStep < lastStep) it.copy(navStep = it.navStep + 1) else it }
        if (_ui.value.navStep == lastStep) { navJob?.cancel(); navJob = null; _ui.update { it.copy(playing = false) } }
    }

    /** @return true bila rekap tersimpan dan UI boleh pindah ke Feed. */
    fun finishNav(): Boolean {
        val s = _ui.value
        if (s.elapsed == 0 && s.navStep == 0) { notify("Jalankan setidaknya satu tahap simulasi dahulu."); return false }
        navJob?.cancel(); navJob = null
        _ui.update { it.copy(playing = false) }
        persist { it.copy(recap = Recap(currentRoute(s).id, progress(s), s.elapsed, "Sesi contoh lokal")) }
        notify("Rekap simulasi tersimpan di perangkat.")
        return true
    }

    fun addReport(type: String, note: String) {
        persist { st -> st.copy(reports = (listOf(Report(System.currentTimeMillis(), type, note.trim().take(160))) + st.reports).take(12)) }
        notify("Laporan contoh hanya tersimpan lokal.")
    }

    // ---- Buddy ----
    fun setBuddySport(v: String) = _ui.update { it.copy(buddySport = v) }
    fun setBuddyStyle(v: String) = _ui.update { it.copy(buddyStyle = v) }
    fun events(s: UiState): List<Event> = seed.events.filter {
        (s.buddySport == "all" || s.buddySport == it.sport) && (s.buddyStyle == "all" || s.buddyStyle == it.style)
    }
    fun toggleInterest(id: String) {
        persist { st -> st.copy(eventInterest = if (id in st.eventInterest) st.eventInterest - id else st.eventInterest + id) }
        notify("Preferensi agenda tersimpan hanya di perangkat ini.")
    }

    // ---- Pit-stop ----
    fun setStopFilter(v: String) = _ui.update { it.copy(stopFilter = v) }
    fun stops(s: UiState, st: Stored): List<Stop> {
        val all = seed.stops + st.customPromos.mapIndexed { i, p ->
            Stop("custom-$i", p.merchant, "Merchant buatan pengguna · hanya di perangkat", p.price, false,
                "Belum ada fasilitas terverifikasi", p.offer, "green", custom = true)
        }
        return all.filter {
            s.stopFilter == "all" || (s.stopFilter == "budget" && it.price < 15000) || (s.stopFilter == "bike" && it.bike)
        }
    }
    fun claimVoucher() = persist { it.copy(voucherInterest = it.voucherInterest + 1) }

    // ---- Feed ----
    fun feed(st: Stored): List<FeedPost> =
        st.posts.mapIndexed { i, t -> FeedPost("self-$i", "Catatan pribadiku", "K", t, "Hanya di perangkat ini") } + seed.feed

    fun savePost(text: String): Boolean {
        val t = text.trim()
        if (t.length < 5) { notify("Tulis setidaknya lima karakter."); return false }
        persist { st -> st.copy(posts = (listOf(t.take(180)) + st.posts).take(8)) }
        notify("Catatan disimpan secara lokal, tidak dipublikasikan.")
        return true
    }
    fun toggleKudos(id: String) = persist { st -> st.copy(kudos = if (id in st.kudos) st.kudos - id else st.kudos + id) }

    // ---- Mitra ----
    fun addPromo(merchant: String, offer: String, priceText: String): Boolean {
        val m = merchant.trim(); val o = offer.trim(); val price = priceText.trim().toIntOrNull()
        if (m.length < 3 || o.length < 4 || price == null || price < 0 || price > 1_000_000) {
            notify("Periksa nama, promo, dan harga contoh."); return false
        }
        persist { st -> st.copy(customPromos = (listOf(CustomPromo(m.take(45), o.take(58), price)) + st.customPromos).take(10)) }
        _ui.update { it.copy(stopFilter = "all") }
        notify("Promo contoh muncul di Pit-stop, hanya di perangkat.")
        return true
    }

    private fun persist(transform: (Stored) -> Stored) {
        viewModelScope.launch { repo.update(transform) }
    }
}
