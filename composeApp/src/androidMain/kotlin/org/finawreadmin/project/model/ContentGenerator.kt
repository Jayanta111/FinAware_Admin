package org.finawreadmin.project.model

import android.util.Log
import com.google.ai.client.generativeai.GenerativeModel
import org.finawreadmin.project.BuildConfig
import org.finawreadmin.project.Database.ContentRepository

actual class ContentGeneratorAI actual constructor(private val apiKey: String) {

    private  val model = GenerativeModel(
        modelName = "gemini-2.5-flash",
        apiKey = BuildConfig.FIREBASE_GEMINI_API_KEY
    )

    actual suspend fun generateLearningEntry(
        title: String,
        language: String,
        courseId: String
    ): LearningEntry? {
        return try {
            val prompt = buildPrompt(title, language)

            val response = model.generateContent(prompt)

            val generatedText = response.text ?: return null

            val (intro, example, prevention) = extractSections(generatedText)

            val entry = LearningEntry(
                courseId = courseId,
                title = title,
                intro = intro,
                example = example,
                prevention = prevention,
                language = language,

            )

            ContentRepository().saveContent(courseId, entry)

            return entry
        } catch (e: Exception) {
            Log.e("ContentGeneratorAI", "Error generating AI content: ${e.message}", e)
            null
        }
    }

    private fun buildPrompt(title: String, language: String): String {
        val baseStructure = """
          AS you are  expert in Finacial Teacher and Fraud Awareness  
        Generate an educational module for the topic "$title".
        The module should include the following sections:

        1. 📘 **Introduction**: Briefly explain the concept of "$title" in a way that is easy to understand for students or general users.
        2. 📍 **Example / Case Study**: Give a realistic example, story, or case study relevant to "$title", especially in the context of India.
        3. 🛡️ **Prevention / Tips**: Share practical tips or advice to avoid mistakes, fraud, or misunderstandings related to "$title".
        
        Use simple, clear language and bullet points where applicable.
    """.trimIndent()

        val localizedPrompt = when (language.lowercase()) {
            "hi" -> """
            "$title" विषय पर एक शिक्षाप्रद पाठ तैयार करें। पाठ में निम्नलिखित अनुभाग शामिल हों:
            
            1. 📘 **परिचय**: "$title" की सरल और स्पष्ट व्याख्या करें।
            2. 📍 **उदाहरण / केस स्टडी**: भारत के संदर्भ में एक यथार्थवादी उदाहरण या केस स्टडी दें।
            3. 🛡️ **रोकथाम / सुझाव**: धोखाधड़ी या गलतफहमियों से बचने के व्यावहारिक सुझाव दें।
        """.trimIndent()

            "pa" -> """
            "$title" ਵਿਸ਼ੇ 'ਤੇ ਇੱਕ ਸਿੱਖਣ ਯੋਗ ਪਾਠ ਬਣਾਓ। ਇਸ ਵਿੱਚ ਹੇਠਲੇ ਭਾਗ ਸ਼ਾਮਿਲ ਹੋਣ:
            
            1. 📘 **ਜਾਣ-ਪਛਾਣ**: "$title" ਦੀ ਸਧਾਰਣ ਅਤੇ ਆਸਾਨ ਵਿਆਖਿਆ ਕਰੋ।
            2. 📍 **ਉਦਾਹਰਣ / ਕੇਸ ਅਧਿਐਨ**: ਭਾਰਤ ਨਾਲ ਸੰਬੰਧਤ ਇੱਕ ਹਕੀਕਤੀ ਉਦਾਹਰਣ ਦਿਓ।
            3. 🛡️ **ਬਚਾਅ / ਸੁਝਾਅ**: ਧੋਖੇ ਜਾਂ ਗਲਤ ਫਹਿਮੀਆਂ ਤੋਂ ਬਚਣ ਲਈ ਅਮਲੀ ਸੁਝਾਅ ਦਿਓ।
        """.trimIndent()

            "as" -> """
            "$title" বিষয়ে এটা শিক্ষামূলক পাঠ সাজাও। তলত উল্লেখ কৰা অংশবোৰ অন্তর্ভুক্ত কৰক:
            
            1. 📘 **পৰিচয়**: "$title" ৰ সহজ আৰু বুজাব পৰা ব্যাখ্যা দিয়া।
            2. 📍 **উদাহৰণ / কেচ ষ্টাডি**: ভাৰতৰ পৰিপ্ৰেক্ষিতত এটা বাস্তৱ উদাহৰণ দিয়া।
            3. 🛡️ **প্ৰতিরোধ / পৰামৰ্শ**: ঠগি বা ভুল বুজাৰ পৰা ৰক্ষা পাবলৈ কাৰ্যকৰী পৰামৰ্শ দিয়া।
        """.trimIndent()

            else -> baseStructure
        }

        return localizedPrompt
    }


    private fun extractSections(text: String): Triple<String, String, String> {
        val parts = text.split("\n\n").map { it.trim() }.filter { it.isNotEmpty() }
        val intro = parts.getOrNull(0) ?: ""
        val example = parts.getOrNull(1) ?: ""
        val prevention = parts.getOrNull(2) ?: ""
        return Triple(intro, example, prevention)
    }
}