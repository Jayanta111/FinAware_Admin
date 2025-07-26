package org.finawreadmin.project.model

import kotlinx.serialization.Serializable

// ✅ Request model sent to Gemini API
@Serializable
data class GeminiRequest(
    val contents: List<GeminiContent>
)

@Serializable
data class GeminiContent(
    val parts: List<GeminiPart>
)

@Serializable
data class GeminiPart(
    val text: String
)

// ✅ Response model received from Gemini API
@Serializable
data class GeminiResponse(
    val candidates: List<GeminiCandidate>? = null
)

@Serializable
data class GeminiCandidate(
    val content: GeminiContentResponse
)

@Serializable
data class GeminiContentResponse(
    val parts: List<GeminiPartResponse>
)

@Serializable
data class GeminiPartResponse(
    val text: String
)
