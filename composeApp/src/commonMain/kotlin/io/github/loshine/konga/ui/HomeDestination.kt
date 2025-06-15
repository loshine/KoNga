package io.github.loshine.konga.ui

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.window.core.layout.WindowWidthSizeClass
import com.github.panpf.sketch.AsyncImage
import com.github.panpf.sketch.rememberAsyncImageState
import com.github.panpf.sketch.request.ComposableImageOptions
import com.github.panpf.sketch.request.error
import com.github.panpf.sketch.request.placeholder
import io.github.loshine.konga.data.entity.ForumGroup
import io.github.loshine.konga.theme.AppTheme
import konga.composeapp.generated.resources.Res
import konga.composeapp.generated.resources.home
import konga.composeapp.generated.resources.ic_forum_placeholder
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.koinInject


@Composable
fun HomeDestination(viewModel: HomeViewModel = koinInject()) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    HomeScreen(
        uiState = uiState,
        onRefresh = {
            viewModel.refresh(forceRefresh = true)
        },
        onForumGroupClick = { group, expanded ->
            viewModel.toggleExpanded(group, !expanded)
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    uiState: HomeUiState,
    onRefresh: () -> Unit,
    onForumGroupClick: (ForumGroup, Boolean) -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(Res.string.home)) },
                actions = {

                }
            )
        }
    ) { contentPadding ->
        PullToRefreshBox(
            isRefreshing = uiState.isRefreshing,
            onRefresh = onRefresh
        ) {
            val windowAdaptiveInfo = currentWindowAdaptiveInfo()
            val column = when (windowAdaptiveInfo.windowSizeClass.windowWidthSizeClass) {
                WindowWidthSizeClass.COMPACT -> 1
                WindowWidthSizeClass.MEDIUM -> 2
                else -> 3
            }
            LazyVerticalGrid(
                columns = GridCells.Fixed(column),
                modifier = Modifier.padding(contentPadding)
            ) {
                uiState.forumGroups.forEach { (group, expanded) ->
                    if (group.forums.isEmpty()) return@forEach
                    item(
                        span = { GridItemSpan(column) },
                        contentType = "group"
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth().clickable {
                                onForumGroupClick.invoke(group, expanded)
                            }.padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                group.name.takeIf { it.isNotBlank() } ?: group.id,
                                style = MaterialTheme.typography.titleMedium,
                                modifier = Modifier.weight(1f)
                            )
                            val rotateDegrees by animateFloatAsState(if (expanded) 0f else -180f)
                            Icon(
                                Icons.Default.ExpandLess,
                                contentDescription = if (expanded) "collapse" else "expand",
                                modifier = Modifier.rotate(rotateDegrees)
                            )
                        }
                    }
                    if (expanded) {
                        items(
                            group.forums.size,
                            contentType = { "forum" },
                            key = { index ->
                                val forum = group.forums[index]
                                "${group.id}_${forum.fid}${forum.stid ?: ""}"
                            }
                        ) { index ->
                            val forum = group.forums[index]
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(16.dp),
                                modifier = Modifier.padding(16.dp).animateItem()
                            ) {
                                AsyncImage(
                                    uri = "${uiState.forumIconPath}/app/${forum.fid}.png",
                                    state = rememberAsyncImageState(ComposableImageOptions {
                                        placeholder(Res.drawable.ic_forum_placeholder)
                                        error(Res.drawable.ic_forum_placeholder)
                                        resizeOnDraw(true)
                                        crossfade(true)
                                    }),
                                    contentDescription = forum.name,
                                    modifier = Modifier.size(40.dp)
                                )
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(forum.name)
                                    forum.description?.also {
                                        Text(
                                            it,
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Preview
@Composable
private fun HomeScreenPreview() {
    AppTheme {
        HomeScreen(
            uiState = HomeUiState(),
            onRefresh = {},
            onForumGroupClick = { _, _ -> }
        )
    }
}
