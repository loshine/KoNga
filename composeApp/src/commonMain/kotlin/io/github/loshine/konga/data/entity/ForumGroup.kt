package io.github.loshine.konga.data.entity

import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

data class ForumGroup(
    val id: String,
    val name: String,
    val forums: ImmutableList<Forum> = persistentListOf()
)
