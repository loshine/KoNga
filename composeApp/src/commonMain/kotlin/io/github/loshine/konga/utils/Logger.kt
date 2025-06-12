package io.github.loshine.konga.utils

import io.github.aakira.napier.DebugAntilog
import io.github.aakira.napier.Napier

object Logger {

    enum class LogLevel {
        VERBOSE,

        DEBUG,

        INFO,

        WARNING,

        ERROR,

        ASSERT;
    }

    fun init(defaultTag: String = "konga") {
        Napier.base(DebugAntilog(defaultTag))
    }

    fun verbose(message: String, throwable: Throwable? = null, tag: String? = null) =
        Napier.v(message, throwable, tag)

    fun verbose(throwable: Throwable? = null, tag: String? = null, message: () -> String) =
        Napier.v(throwable, tag, message)

    fun info(message: String, throwable: Throwable? = null, tag: String? = null) =
        Napier.i(message, throwable, tag)

    fun info(throwable: Throwable? = null, tag: String? = null, message: () -> String) =
        Napier.i(throwable, tag, message)

    fun debug(message: String, throwable: Throwable? = null, tag: String? = null) =
        Napier.d(message, throwable, tag)

    fun debug(throwable: Throwable? = null, tag: String? = null, message: () -> String) =
        Napier.d(throwable, tag, message)

    fun warning(message: String, throwable: Throwable? = null, tag: String? = null) =
        Napier.w(message, throwable, tag)

    fun warning(throwable: Throwable? = null, tag: String? = null, message: () -> String) =
        Napier.w(throwable, tag, message)

    fun error(message: String, throwable: Throwable? = null, tag: String? = null) =
        Napier.e(message, throwable, tag)

    fun error(throwable: Throwable? = null, tag: String? = null, message: () -> String) =
        Napier.e(throwable, tag, message)

    fun wtf(message: String, throwable: Throwable? = null, tag: String? = null) =
        Napier.wtf(message, throwable, tag)

    fun wtf(throwable: Throwable? = null, tag: String? = null, message: () -> String) =
        Napier.wtf(throwable, tag, message)

    fun log(
        priority: LogLevel,
        tag: String? = null,
        throwable: Throwable? = null,
        message: String
    ) = Napier.log(priority.toNapierLogLevel(), tag, throwable, message)

    fun clear() {
        Napier.takeLogarithm()
    }

    private fun LogLevel.toNapierLogLevel(): io.github.aakira.napier.LogLevel {
        return when (this) {
            LogLevel.VERBOSE -> io.github.aakira.napier.LogLevel.VERBOSE
            LogLevel.DEBUG -> io.github.aakira.napier.LogLevel.DEBUG
            LogLevel.INFO -> io.github.aakira.napier.LogLevel.INFO
            LogLevel.WARNING -> io.github.aakira.napier.LogLevel.WARNING
            LogLevel.ERROR -> io.github.aakira.napier.LogLevel.ERROR
            LogLevel.ASSERT -> io.github.aakira.napier.LogLevel.ASSERT
        }
    }
}
