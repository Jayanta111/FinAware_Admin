package org.finawreadmin.project.Database

import com.google.firebase.firestore.FirebaseFirestore
import org.finAware.project.data.model.ContentEntry
import kotlinx.coroutines.tasks.await
import org.finawreadmin.project.model.LearningEntry

object ContentRepository {
    private val db = FirebaseFirestore.getInstance()
    private val contentCollection = db.collection("learning_contents")

    suspend fun saveContent(entry: LearningEntry): Result<Unit> {
        return try {
            contentCollection.add(entry).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}

