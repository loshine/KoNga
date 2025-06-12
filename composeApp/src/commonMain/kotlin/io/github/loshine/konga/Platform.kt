package io.github.loshine.konga

import com.fleeksoft.charset.Charsets
import com.fleeksoft.charset.decodeToString
import io.ktor.client.HttpClient
import io.ktor.client.HttpClientConfig
import io.ktor.client.call.HttpClientCall
import io.ktor.client.plugins.HttpClientPlugin
import io.ktor.client.plugins.HttpRequestRetry
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.UserAgent
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.request.header
import io.ktor.client.statement.HttpReceivePipeline
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsBytes
import io.ktor.http.Headers
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpProtocolVersion
import io.ktor.http.HttpStatusCode
import io.ktor.http.URLProtocol
import io.ktor.util.AttributeKey
import io.ktor.util.date.GMTDate
import io.ktor.utils.io.ByteReadChannel
import io.ktor.utils.io.InternalAPI
import kotlin.coroutines.CoroutineContext

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform

expect fun httpClient(
    config: HttpClientConfig<*>.() -> Unit = {
        install(HttpTimeout) {
            requestTimeoutMillis = 60_000
            connectTimeoutMillis = 10_000
            socketTimeoutMillis = 10_000
        }
        defaultRequest {
            url {
                protocol = URLProtocol.HTTPS
                host = "bbs.nga.cn"
//                path("docs/")
                parameters.append("lite", "xml")
            }
            header("key", "value")
        }
        install(UserAgent) {
            agent = "Ktor client"
        }
        install(HttpRequestRetry) {
            retryOnServerErrors(maxRetries = 5)
            exponentialDelay()
        }
        install(ContentNegotiation)
//        install(Logging) {
//            logger = object : Logger {
//                override fun log(message: String) {
//                    LoggerUtils.debug("HTTP Client", null, message)
//                }
//            }
//            level = LogLevel.HEADERS
//        }
        install(Gb18030Support)
    }
): HttpClient

object Gb18030Support : HttpClientPlugin<Unit, Gb18030Support> {
    override val key: AttributeKey<Gb18030Support> = AttributeKey("Gb18030Support")

    override fun prepare(block: Unit.() -> Unit): Gb18030Support = this

    override fun install(plugin: Gb18030Support, scope: HttpClient) {
        scope.receivePipeline.intercept(HttpReceivePipeline.State) { response ->
            val contentType = response.headers[HttpHeaders.ContentType]
            if (contentType?.contains("gb18030", ignoreCase = true) == true) {
                val rawBytes = response.bodyAsBytes()
                val decoded = rawBytes.decodeToString(Charsets.forName("GB18030"))
                val newChannel = ByteReadChannel(decoded.encodeToByteArray())
                proceedWith(ModifiedHttpResponse(original = response, rawContent = newChannel))
            } else {
                proceedWith(response)
            }
        }
    }
}

class ModifiedHttpResponse(
    private val original: HttpResponse,
    @InternalAPI
    override val rawContent: ByteReadChannel,
) : HttpResponse() {
    override val call: HttpClientCall get() = original.call
    override val status: HttpStatusCode get() = original.status
    override val version: HttpProtocolVersion get() = original.version
    override val requestTime: GMTDate get() = original.requestTime
    override val responseTime: GMTDate get() = original.responseTime
    override val headers: Headers get() = original.headers
    override val coroutineContext: CoroutineContext get() = original.coroutineContext
}
