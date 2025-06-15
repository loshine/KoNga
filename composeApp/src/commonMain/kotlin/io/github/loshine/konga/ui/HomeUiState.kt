package io.github.loshine.konga.ui

import io.github.loshine.konga.data.entity.ForumGroup
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

data class HomeUiState(
    val isRefreshing: Boolean = false,
    val forumGroups: ImmutableList<Pair<ForumGroup, Boolean>> = persistentListOf(),
    val doubleColumnFirst: ImmutableList<String> = persistentListOf(),
    val doubleColumnSecond: ImmutableList<String> = persistentListOf(),
    val singleColumn: ImmutableList<String> = persistentListOf(),
    val forumIconPath: String = ""
)
