package io.github.loshine.konga

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBox
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Home
import androidx.compose.ui.graphics.vector.ImageVector
import konga.composeapp.generated.resources.Res
import konga.composeapp.generated.resources.favorites
import konga.composeapp.generated.resources.histories
import konga.composeapp.generated.resources.home
import konga.composeapp.generated.resources.profile
import org.jetbrains.compose.resources.StringResource

enum class AppDestinations(
    val label: StringResource,
    val icon: ImageVector,
    val contentDescription: StringResource
) {
    HOME(
        Res.string.home,
        Icons.Default.Home,
        Res.string.home
    ),
    FAVORITES(
        Res.string.favorites,
        Icons.Default.Favorite,
        Res.string.favorites
    ),
    HISTORIES(
        label = Res.string.histories,
        Icons.Default.History,
        contentDescription = Res.string.histories
    ),
    PROFILE(
        Res.string.profile,
        Icons.Default.AccountBox,
        Res.string.profile
    ),
}
