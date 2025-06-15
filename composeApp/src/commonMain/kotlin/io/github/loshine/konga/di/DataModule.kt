package io.github.loshine.konga.di

import io.github.loshine.konga.data.source.remote.httpClient
import kotlinx.serialization.json.Json
import org.koin.core.annotation.Factory
import org.koin.core.annotation.Module
import org.koin.core.annotation.Single

@Module
class DataModule {

    @Factory
    @Single
    fun httpClientFactory() = httpClient()

    @Factory
    @Single
    fun json() = Json {
        ignoreUnknownKeys = true
    }
}
