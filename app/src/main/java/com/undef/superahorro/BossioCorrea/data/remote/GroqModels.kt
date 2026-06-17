package com.undef.superahorro.BossioCorrea.data.remote

import com.google.gson.annotations.SerializedName

// ── Request ───────────────────────────────────────────────────────────────────

data class GroqRequest(
    val model: String,
    @SerializedName("max_tokens") val maxTokens: Int,
    val temperature: Double,
    @SerializedName("response_format") val responseFormat: ResponseFormat? = null,
    val messages: List<GroqMessage>
)

data class GroqMessage(
    val role: String,
    // String para mensajes de texto simple; List<GroqContentPart> para vision (imagen + texto)
    val content: Any
)

data class GroqContentPart(
    val type: String,
    val text: String? = null,
    @SerializedName("image_url") val imageUrl: GroqImageUrl? = null
)

data class GroqImageUrl(
    val url: String
)

data class ResponseFormat(
    val type: String
)

// ── Response ──────────────────────────────────────────────────────────────────

data class GroqResponse(
    val choices: List<GroqChoice>
)

data class GroqChoice(
    val message: GroqMessageContent
)

data class GroqMessageContent(
    val content: String
)
