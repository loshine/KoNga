package io.github.loshine.konga.data.entity

import kotlinx.serialization.Serializable

@Serializable
data class Forum(
    val fid: Long,
    val name: String,
    val info: String? = null,
    val infoL: String? = null,
    val stid: Long? = null,
    val bit: Int? = null
) {
    val forumId
        get() = stid ?: fid

    val description
        get() = infoL ?: info
}
