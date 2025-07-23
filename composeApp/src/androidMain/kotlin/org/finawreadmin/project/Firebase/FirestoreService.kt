package org.finAware.project.firebase

import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import org.finawreadmin.project.model.LearningEntry

object FirestoreService {
    private val firestore = FirebaseFirestore.getInstance()
    private val contentCollection = firestore.collection("learningContent")

    // Save a learning entry using courseId as document ID
    suspend fun saveLearningEntry(entry: LearningEntry): Result<Unit> = try {
        contentCollection.document(entry.courseId).set(entry).await()
        Result.success(Unit)
    } catch (e: Exception) {
        Result.failure(e)
    }

    // Fetch all learning entries
    suspend fun getAllLearningEntries(): List<LearningEntry> {
        return try {
            val snapshot = contentCollection.orderBy("timestamp").get().await()
            snapshot.documents.mapNotNull { doc ->
                val courseId = doc.id
                val title = doc.getString("title") ?: return@mapNotNull null
                val intro = doc.getString("intro") ?: return@mapNotNull null
                val imageUrl = doc.getString("imageUrl")
                val example = doc.getString("example") ?: ""
                val prevention = doc.getString("prevention") ?: ""
                val quiz = doc.getString("quiz") ?: ""
                val language = doc.getString("language") ?: ""

                LearningEntry(
                    courseId = courseId,
                    title = title,
                    intro = intro,
                    imageUrl = imageUrl,
                    example = example,
                    prevention = prevention,
                    quiz = quiz,
                    language = language
                )
            }
        } catch (e: Exception) {
            emptyList()
        }
    }
}
