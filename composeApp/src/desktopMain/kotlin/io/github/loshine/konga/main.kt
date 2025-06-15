package io.github.loshine.konga

import androidx.compose.runtime.SideEffect
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import io.github.loshine.konga.utils.Logger
import java.awt.Dimension

fun main() = application {
    Logger.init()
    Window(
        onCloseRequest = ::exitApplication,
        title = "KoNga",
        state = rememberWindowState(),
    ) {
        // 限制窗口最小尺寸
        SideEffect {
            window.minimumSize = Dimension(400, 300)
        }

        App()
    }
}
