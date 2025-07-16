package org.finawreadmin.project.API

import com.google.generativeai.GenerativeModel
import com.google.generativeai.type.History
import com.google.generativeai.type.SystemInstruction
import org.finawreadmin.project.BuildConfig

suspend fun callGemini(prompt: String): String? {
    val GEMINI_API_KEY = BuildConfig.GEMINI_API_KEY

    // Initialize the model
    val generativeModel = GenerativeModel(
        modelName = "gemini-1.5-flash",
        apiKey = GEMINI_API_KEY
    )

    val systemInstruction = SystemInstruction.from("You are an expert educational content generator.")

    val chat = generativeModel.startChat(
        history = History(),
        systemInstruction = systemInstruction
    )

    return try {
        val response = chat.sendMessage(prompt)
        response.text
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}
