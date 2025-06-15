package io.github.loshine.konga.data.entity

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import nl.adaptivity.xmlutil.serialization.XmlElement
import nl.adaptivity.xmlutil.serialization.XmlSerialName

@Serializable
@XmlSerialName("root")
data class ForumRoot(
    @XmlElement(true)
    @XmlSerialName("__GLOBAL")
    val global: Global? = null,

    @XmlElement(true)
    @XmlSerialName("__ROWS")
    val rows: Long? = null,

    @XmlElement(true)
    @XmlSerialName("__F")
    val forumDetails: ForumDetails
)

@Serializable
@SerialName("__F")
data class ForumDetails(
    @XmlElement(true)
    val fid: String,
    @XmlElement(true)
    val name: String,
    @XmlElement(true)
    @XmlSerialName("topped_topic")
    val toppedTopic: String
)
