package io.github.loshine.konga

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import io.github.loshine.konga.data.entity.ForumRoot
import io.github.loshine.konga.data.source.remote.httpClient
import io.github.loshine.konga.di.AppModule
import io.github.loshine.konga.theme.AppTheme
import io.github.loshine.konga.theme.darkScheme
import io.github.loshine.konga.theme.lightScheme
import io.github.loshine.konga.ui.FavoritesDestination
import io.github.loshine.konga.ui.HistoriesDestination
import io.github.loshine.konga.ui.HomeDestination
import io.github.loshine.konga.ui.ProfileDestination
import io.github.loshine.konga.utils.Logger
import io.ktor.client.call.body
import io.ktor.client.request.get
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.KoinApplication
import org.koin.ksp.generated.module

@Composable
fun App(
    darkTheme: Boolean = isSystemInDarkTheme(),
    colorScheme: ColorScheme = if (darkTheme) darkScheme else lightScheme,
) {
    KoinApplication({
        modules(AppModule().module)
    }) {
        AppTheme(
            darkTheme = darkTheme,
            colorScheme = colorScheme
        ) {
            var currentDestination by rememberSaveable { mutableStateOf(AppDestinations.HOME) }
            LaunchedEffect(Unit) {
                val client = httpClient()
                val root = client.get("thread.php?fid=-152678").body<ForumRoot>()
                Logger.debug { root.toString() }
            }
            NavigationSuiteScaffold(
                navigationSuiteItems = {
                    AppDestinations.entries.forEach {
                        item(
                            icon = {
                                Icon(
                                    it.icon,
                                    contentDescription = stringResource(it.contentDescription)
                                )
                            },
                            label = { Text(stringResource(it.label)) },
                            selected = it == currentDestination,
                            onClick = { currentDestination = it }
                        )
                    }
                }
            ) {
                AppNavigationContent(currentDestination)
            }
        }
    }
}


@Composable
private fun AppNavigationContent(currentDestination: AppDestinations) {
    // Destination content.
    when (currentDestination) {
        AppDestinations.HOME -> HomeDestination()
        AppDestinations.FAVORITES -> FavoritesDestination()
        AppDestinations.HISTORIES -> HistoriesDestination()
        AppDestinations.PROFILE -> ProfileDestination()
    }
}
