package id.explorstreet.app.ui.components

import android.location.Location
import android.os.Bundle
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import org.maplibre.android.MapLibre
import org.maplibre.android.camera.CameraPosition
import org.maplibre.android.geometry.LatLng
import org.maplibre.android.maps.MapLibreMap
import org.maplibre.android.maps.MapView
import org.maplibre.android.maps.Style
import org.maplibre.android.style.layers.CircleLayer
import org.maplibre.android.style.layers.PropertyFactory.circleColor
import org.maplibre.android.style.layers.PropertyFactory.circleRadius
import org.maplibre.android.style.layers.PropertyFactory.circleStrokeColor
import org.maplibre.android.style.layers.PropertyFactory.circleStrokeWidth
import org.maplibre.android.style.sources.GeoJsonSource
import org.maplibre.geojson.Feature
import org.maplibre.geojson.Point

/** Tile raster OpenStreetMap — gratis, tanpa API key. Wajib tampilkan atribusi © OpenStreetMap. */
private const val OSM_STYLE = """
{
  "version": 8,
  "sources": {
    "osm": {
      "type": "raster",
      "tiles": ["https://tile.openstreetmap.org/{z}/{x}/{y}.png"],
      "tileSize": 256,
      "maxzoom": 19,
      "attribution": "© OpenStreetMap contributors"
    }
  },
  "layers": [{ "id": "osm", "type": "raster", "source": "osm" }]
}
"""

@Composable
fun OsmMap(startLat: Double, startLng: Double, me: Location?, modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val lifecycle = LocalLifecycleOwner.current.lifecycle
    val mapView = remember {
        MapLibre.getInstance(context)
        MapView(context).apply { onCreate(Bundle()) }
    }
    val holder = remember { arrayOfNulls<MapLibreMap>(1) }

    DisposableEffect(lifecycle) {
        val obs = LifecycleEventObserver { _, e ->
            when (e) {
                Lifecycle.Event.ON_START -> mapView.onStart()
                Lifecycle.Event.ON_RESUME -> mapView.onResume()
                Lifecycle.Event.ON_PAUSE -> mapView.onPause()
                Lifecycle.Event.ON_STOP -> mapView.onStop()
                else -> {}
            }
        }
        lifecycle.addObserver(obs)
        mapView.onStart(); mapView.onResume()
        onDispose { lifecycle.removeObserver(obs); mapView.onPause(); mapView.onStop(); mapView.onDestroy() }
    }

    LaunchedEffect(Unit) {
        mapView.getMapAsync { map ->
            holder[0] = map
            map.setStyle(Style.Builder().fromJson(OSM_STYLE)) { style ->
                style.addSource(GeoJsonSource("start", Feature.fromGeometry(Point.fromLngLat(startLng, startLat))))
                style.addLayer(CircleLayer("start", "start").withProperties(
                    circleRadius(8f), circleColor("#174C37"), circleStrokeColor("#FFFFFF"), circleStrokeWidth(3f)))
                style.addSource(GeoJsonSource("me"))
                style.addLayer(CircleLayer("me", "me").withProperties(
                    circleRadius(8f), circleColor("#2E7CF6"), circleStrokeColor("#FFFFFF"), circleStrokeWidth(3f)))
            }
            map.cameraPosition = CameraPosition.Builder().target(LatLng(startLat, startLng)).zoom(14.5).build()
        }
    }

    LaunchedEffect(startLat, startLng) {
        holder[0]?.let { map ->
            map.style?.getSourceAs<GeoJsonSource>("start")?.setGeoJson(Feature.fromGeometry(Point.fromLngLat(startLng, startLat)))
            map.cameraPosition = CameraPosition.Builder().target(LatLng(startLat, startLng)).zoom(14.5).build()
        }
    }
    LaunchedEffect(me) {
        val loc = me ?: return@LaunchedEffect
        holder[0]?.style?.getSourceAs<GeoJsonSource>("me")?.setGeoJson(Feature.fromGeometry(Point.fromLngLat(loc.longitude, loc.latitude)))
    }

    AndroidView({ mapView }, modifier.fillMaxWidth().height(260.dp).clip(CardShape))
}
