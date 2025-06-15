package io.github.loshine.konga.data.source.remote

import com.fleeksoft.charset.Charsets
import com.fleeksoft.charset.decodeToString
import io.github.loshine.konga.utils.Logger
import io.ktor.client.HttpClient
import io.ktor.client.HttpClientConfig
import io.ktor.client.call.HttpClientCall
import io.ktor.client.plugins.HttpClientPlugin
import io.ktor.client.plugins.HttpRequestRetry
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.UserAgent
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.request.header
import io.ktor.client.statement.HttpReceivePipeline
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsBytes
import io.ktor.http.Headers
import io.ktor.http.HttpProtocolVersion
import io.ktor.http.HttpStatusCode
import io.ktor.http.URLProtocol
import io.ktor.serialization.kotlinx.xml.xml
import io.ktor.util.AttributeKey
import io.ktor.util.date.GMTDate
import io.ktor.utils.io.ByteReadChannel
import io.ktor.utils.io.InternalAPI
import io.ktor.utils.io.core.toByteArray
import nl.adaptivity.xmlutil.ExperimentalXmlUtilApi
import nl.adaptivity.xmlutil.XmlDeclMode
import nl.adaptivity.xmlutil.serialization.DefaultXmlSerializationPolicy
import nl.adaptivity.xmlutil.serialization.XML
import kotlin.coroutines.CoroutineContext
import io.ktor.client.plugins.logging.Logger as KtorLogger

@OptIn(ExperimentalXmlUtilApi::class)
expect fun httpClient(
    config: HttpClientConfig<*>.() -> Unit = {
        install(HttpTimeout) {
            // 整个请求的超时时间（包括连接、发送、接收）
            requestTimeoutMillis = 30_000L
            // TCP 连接建立的最大等待时间
            connectTimeoutMillis = 10_000L
            // 每次 socket 读写的超时
            socketTimeoutMillis = 15_000L
        }
        defaultRequest {
            url {
                protocol = URLProtocol.HTTPS
                host = "bbs.nga.cn"
                parameters.append("lite", "xml")
            }
            header("key", "value")
        }
        install(UserAgent) {
            agent = "NGA_skull/7.3.1(iPhone13,2;iOS 15.5)"
        }
        install(HttpRequestRetry) {
            retryOnServerErrors(maxRetries = 5)
            exponentialDelay()
        }
        install(ContentNegotiation) {
            xml(format = XML {
                xmlDeclMode = XmlDeclMode.Auto
                policy = DefaultXmlSerializationPolicy.Builder().apply {
                    ignoreUnknownChildren()
                }.build()
            })
        }
        install(Logging) {
            logger = object : KtorLogger {
                override fun log(message: String) {
                    Logger.info("HTTP Client", null, message)
                }
            }
            level = LogLevel.INFO
        }
        install(Gb18030Support)
    }
): HttpClient

object Gb18030Support : HttpClientPlugin<Unit, Gb18030Support> {

    val charset by lazy { Charsets.forName("GB18030") }

    private const val PREFIX = "<?xml version=\"1.0\" encoding=\"GB18030\"?>"
    private val prefixBytes by lazy { PREFIX.toByteArray() }

    override val key: AttributeKey<Gb18030Support> = AttributeKey("Gb18030Support")

    override fun prepare(block: Unit.() -> Unit): Gb18030Support = this

    override fun install(plugin: Gb18030Support, scope: HttpClient) {
        scope.receivePipeline.intercept(HttpReceivePipeline.State) { response ->
            val rawBytes = response.bodyAsBytes()
            if (rawBytes.startsWith(prefixBytes)) {
                val decoded = rawBytes.decodeToString(charset)
                val newChannel = ByteReadChannel(decoded.encodeToByteArray())
                proceedWith(ModifiedHttpResponse(original = response, rawContent = newChannel))
            } else {
                proceedWith(response)
            }
        }
    }

    private fun ByteArray.startsWith(prefix: ByteArray): Boolean {
        if (this.size < prefix.size) return false
        for (i in prefix.indices) {
            if (this[i] != prefix[i]) return false
        }
        return true
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
