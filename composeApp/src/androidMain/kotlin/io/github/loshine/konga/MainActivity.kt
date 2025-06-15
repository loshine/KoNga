package io.github.loshine.konga

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import io.github.loshine.konga.theme.darkScheme
import io.github.loshine.konga.theme.lightScheme
import io.github.loshine.konga.utils.Logger

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        Logger.init()

        super.onCreate(savedInstanceState)

        setContent {
            val darkTheme = isSystemInDarkTheme()
            val colorScheme = when {
                Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
                    val context = LocalContext.current
                    if (darkTheme)
                        dynamicDarkColorScheme(context)
                    else
                        dynamicLightColorScheme(context)
                }

                darkTheme -> darkScheme
                else -> lightScheme
            }
            App(
                darkTheme = darkTheme,
                colorScheme = colorScheme
            )
        }
    }
}

@Preview
@Composable
fun AppAndroidPreview() {
    App()
}
