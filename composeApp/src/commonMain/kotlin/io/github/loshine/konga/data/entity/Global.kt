package io.github.loshine.konga.data.entity

import kotlinx.serialization.Serializable
import nl.adaptivity.xmlutil.serialization.XmlElement
import nl.adaptivity.xmlutil.serialization.XmlSerialName


@Serializable
data class Global(
    @XmlElement(true)
    @XmlSerialName("_ATTACH_BASE_VIEW", "", "")
    val attachBaseView: String? = null
)
