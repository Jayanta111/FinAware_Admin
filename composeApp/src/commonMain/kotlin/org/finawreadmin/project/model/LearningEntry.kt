package org.finawreadmin.project.model

import kotlinx.serialization.Serializable
import kotlinx.serialization.SerialName
@Serializable
data class LearningEntry(
    @SerialName("_id")
    val id: String? = null,

    val courseId: String? = null,
    val title: String = "",
    val intro: String = "",
    val example: String = "",
    val prevention: String = "",
    val language: String = "",
    val imageUrl: String? = null,
    val imageAvailable: Boolean = false
)