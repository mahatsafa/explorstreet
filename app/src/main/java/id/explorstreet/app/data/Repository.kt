package id.explorstreet.app.data

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.serialization.json.Json

/** Sumber data aplikasi. Saat ini lokal; nanti bisa diganti implementasi Supabase/Firebase. */
interface Repository {
    val seed: Seed
    val stored: Flow<Stored>
    suspend fun update(transform: (Stored) -> Stored)
}

private val Context.dataStore by preferencesDataStore(name = "explorstreet")
private val KEY = stringPreferencesKey("explorstreet-demo-v1")
private val json = Json { ignoreUnknownKeys = true; encodeDefaults = true }

class LocalRepository(private val context: Context) : Repository {
    override val seed: Seed = context.assets.open("seed.json").bufferedReader().use {
        json.decodeFromString(Seed.serializer(), it.readText())
    }

    override val stored: Flow<Stored> = context.dataStore.data.map { prefs ->
        prefs[KEY]?.let { runCatching { json.decodeFromString(Stored.serializer(), it) }.getOrNull() } ?: Stored()
    }

    override suspend fun update(transform: (Stored) -> Stored) {
        context.dataStore.edit { prefs ->
            val current = prefs[KEY]?.let { runCatching { json.decodeFromString(Stored.serializer(), it) }.getOrNull() } ?: Stored()
            prefs[KEY] = json.encodeToString(Stored.serializer(), transform(current))
        }
    }
}
