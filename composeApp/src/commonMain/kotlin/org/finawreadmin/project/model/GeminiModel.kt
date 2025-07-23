package org.finawreadmin.project.model

import kotlinx.serialization.Serializable


@Serializable
data class GeminiRequest(val contents: List<GeminiContent>)

@Serializable
data class GeminiContent(val parts: List<GeminiPart>)

@Serializable
data class GeminiPart(val text: String)

@Serializable
data class GeminiResponse(val candidates: List<GeminiCandidate>? = null)

@Serializable
data class GeminiCandidate(val content: GeminiContentWrapper)

@Serializable
data class GeminiContentWrapper(val parts: List<GeminiPartWrapper>)

@Serializable
data class GeminiPartWrapper(val text: String)