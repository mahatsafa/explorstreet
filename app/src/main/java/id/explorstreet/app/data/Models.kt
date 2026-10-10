package id.explorstreet.app.data

import kotlinx.serialization.Serializable

@Serializable
data class Route(
    val id: String, val name: String, val area: String, val sport: String,
    val km: Double, val min: Int, val climb: Int,
    val quiet: Int, val flat: Int, val coffee: Int,
    val color: String, val lat: Double, val lng: Double,
    val description: String, val path: String, val start: List<Float>,
)

@Serializable
data class Event(
    val id: String, val title: String, val sport: String, val style: String,
    val area: String, val `when`: String, val range: String, val org: String,
)

@Serializable
data class Stop(
    val id: String, val name: String, val area: String, val price: Int, val bike: Boolean,
    val perk: String, val offer: String, val color: String, val custom: Boolean = false,
)

@Serializable
data class FeedPost(val id: String, val author: String, val initial: String, val text: String, val time: String)

@Serializable
data class Seed(
    val routes: List<Route>, val events: List<Event>, val stops: List<Stop>,
    val feed: List<FeedPost>, val navSteps: List<List<String>>,
)

@Serializable
data class Report(val id: Long, val type: String, val note: String)

@Serializable
data class CustomPromo(val merchant: String, val offer: String, val price: Int)

@Serializable
data class Recap(val routeId: String, val progress: Int, val seconds: Int, val created: String)

/** Semua yang di prototipe HTML disimpan di localStorage. */
@Serializable
data class Stored(
    val routePreviewClicks: Int = 0,
    val voucherInterest: Int = 0,
    val eventInterest: List<String> = emptyList(),
    val reports: List<Report> = emptyList(),
    val customPromos: List<CustomPromo> = emptyList(),
    val posts: List<String> = emptyList(),
    val kudos: List<String> = emptyList(),
    val recap: Recap? = null,
)
