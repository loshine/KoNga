package io.github.loshine.konga.utils

import com.russhwolf.settings.StorageSettings
import com.russhwolf.settings.coroutines.FlowSettings
import com.russhwolf.settings.coroutines.toFlowSettings
import com.russhwolf.settings.observable.makeObservable

actual fun createSettings(producePath: () -> String): FlowSettings {
    return StorageSettings().makeObservable().toFlowSettings()
}
