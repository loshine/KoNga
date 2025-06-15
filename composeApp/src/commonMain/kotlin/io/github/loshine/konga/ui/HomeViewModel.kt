package io.github.loshine.konga.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fleeksoft.charset.decodeToString
import io.github.loshine.konga.data.entity.Forum
import io.github.loshine.konga.data.entity.ForumGroup
import io.github.loshine.konga.data.source.remote.Gb18030Support
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.statement.bodyAsBytes
import kotlinx.collections.immutable.toImmutableList
import kotlinx.collections.immutable.toPersistentList
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.decodeFromJsonElement
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import org.koin.android.annotation.KoinViewModel
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

private const val JS_PREFIX = "window.script_muti_get_var_store="

@KoinViewModel
class HomeViewModel(
    private val httpClient: HttpClient,
    private val json: Json,
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> by this::_uiState

    init {
        refresh()
    }

    @OptIn(ExperimentalTime::class)
    fun refresh(forceRefresh: Boolean = false) {
        viewModelScope.launch {
            _uiState.update {
                it.copy(isRefreshing = true)
            }
            runCatching {
                var content = httpClient
                    .get("https://img4.nga.178.com/proxy/cache_attach/bbs_index_data.js") {
                        if (forceRefresh) {
                            parameter("currentMillis", Clock.System.now().toEpochMilliseconds())
                        }
                    }
                    .bodyAsBytes()
                    .decodeToString(Gb18030Support.charset)

                if (content.startsWith(JS_PREFIX)) {
                    content = content.substring(JS_PREFIX.length)
                }

                val jsonObject = json.decodeFromString<JsonObject>(content)
                val dataJsonObject = jsonObject["data"]?.jsonObject?.get("0")?.jsonObject
                val forumGroupJsonObjects = dataJsonObject?.get("all")?.jsonObject

                val forumGroups = (forumGroupJsonObjects?.map { (_, forumGroupValue) ->
                    val forumGroupJsonObject = forumGroupValue.jsonObject
                    val id = forumGroupJsonObject["id"]?.jsonPrimitive?.content.orEmpty()
                    val name = forumGroupJsonObject["name"]?.jsonPrimitive?.content.orEmpty()

                    val content =
                        forumGroupJsonObject["content"]?.jsonObject?.get("0")?.jsonObject?.get("content")?.jsonObject
                    val forums = (content?.map { (_, forumValue) ->
                        json.decodeFromJsonElement<Forum>(forumValue.jsonObject)
                    } ?: emptyList()).toImmutableList()
                    ForumGroup(id = id, name = name, forums = forums)
                } ?: emptyList()).map { it to true }.toPersistentList()

                val doubleColumnFirst =
                    (dataJsonObject?.get("double")?.jsonObject?.get("0")?.jsonObject?.map { (_, value) ->
                        value.jsonPrimitive.content
                    } ?: emptyList()).toImmutableList()

                val doubleColumnSecond =
                    (dataJsonObject?.get("double")?.jsonObject?.get("1")?.jsonObject?.map { (_, value) ->
                        value.jsonPrimitive.content
                    } ?: emptyList()).toImmutableList()

                val singleColumn =
                    (dataJsonObject?.get("single")?.jsonObject?.get("0")?.jsonObject?.map { (_, value) ->
                        value.jsonPrimitive.content
                    } ?: emptyList()).toImmutableList()

                _uiState.value = HomeUiState(
                    forumGroups = forumGroups,
                    doubleColumnFirst = doubleColumnFirst,
                    doubleColumnSecond = doubleColumnSecond,
                    singleColumn = singleColumn,
                    forumIconPath = dataJsonObject?.get("__FORUM_ICON_PATH")?.jsonPrimitive?.content.orEmpty()
                )
            }
            _uiState.update {
                it.copy(isRefreshing = false)
            }
        }
    }

    fun toggleExpanded(group: ForumGroup, expanded: Boolean) {
        _uiState.update { prevState ->
            prevState.copy(forumGroups = prevState.forumGroups.map {
                if (group == it.first) {
                    it.copy(second = expanded)
                } else {
                    it
                }
            }.toImmutableList())
        }
    }
}
