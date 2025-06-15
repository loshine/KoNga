package io.github.loshine.konga.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import konga.composeapp.generated.resources.Res
import konga.composeapp.generated.resources.favorites
import org.jetbrains.compose.resources.stringResource

@Composable
fun FavoritesDestination() {
    Box(contentAlignment = Alignment.Center) {
        Text(stringResource(Res.string.favorites))
    }
}
