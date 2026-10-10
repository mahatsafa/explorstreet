package id.explorstreet.app.location

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.os.Looper
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority

fun hasLocationPermission(context: Context) =
    ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED ||
        ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED

/** Posisi terkini selama layar terlihat. Tidak disimpan dan tidak dikirim. */
@SuppressLint("MissingPermission")
@Composable
fun rememberLiveLocation(enabled: Boolean): State<Location?> {
    val context = LocalContext.current
    val state = remember { mutableStateOf<Location?>(null) }
    DisposableEffect(enabled) {
        if (!enabled || !hasLocationPermission(context)) return@DisposableEffect onDispose { }
        val client = LocationServices.getFusedLocationProviderClient(context)
        val callback = object : LocationCallback() {
            override fun onLocationResult(result: LocationResult) { state.value = result.lastLocation }
        }
        client.lastLocation.addOnSuccessListener { if (it != null && state.value == null) state.value = it }
        client.requestLocationUpdates(
            LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 3000L).build(), callback, Looper.getMainLooper(),
        )
        onDispose { client.removeLocationUpdates(callback) }
    }
    return state
}
