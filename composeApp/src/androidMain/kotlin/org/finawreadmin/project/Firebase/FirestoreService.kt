package org.finAware.project.firebase

import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import org.finawreadmin.project.model.LearningEntry

object FirestoreService {
    private val firestore = FirebaseFirestore.getInstance()

    suspend fun saveEntry(topic: String, content: String) {
        val entry = hashMapOf(
            "topic" to topic,
            "content" to content,
            "timestamp" to System.currentTimeMillis()
        )
        firestore.collection("ai_learning_content").add(entry).await()
    }

    suspend fun getAllEntries(): List<LearningEntry> {
        val snapshot = firestore.collection("ai_learning_content")
            .orderBy("timestamp")
            .get()
            .await()

        return snapshot.documents.mapNotNull {
            val topic = it.getString("topic")
            val content = it.getString("content")
            if (topic != null && content != null) {
                LearningEntry(topic, content)
            } else null
        }
    }
}
