package io.github.loshine.konga.utils

import com.russhwolf.settings.coroutines.FlowSettings

internal const val dataStoreFileName = "app.preferences_pb"

// Common
expect fun createSettings(producePath: () -> String): FlowSettings
