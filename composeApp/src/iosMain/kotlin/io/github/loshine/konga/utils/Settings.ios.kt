package io.github.loshine.konga.utils

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import com.russhwolf.settings.coroutines.FlowSettings
import com.russhwolf.settings.datastore.DataStoreSettings
import okio.Path.Companion.toPath

actual fun createSettings(producePath: () -> String): FlowSettings {
    return DataStoreSettings(createDataStore(producePath))
}

/**
 * Gets the singleton DataStore instance, creating it if necessary.
 */
private fun createDataStore(producePath: () -> String): DataStore<Preferences> =
    PreferenceDataStoreFactory.createWithPath(
        produceFile = { producePath().toPath() }
    )
