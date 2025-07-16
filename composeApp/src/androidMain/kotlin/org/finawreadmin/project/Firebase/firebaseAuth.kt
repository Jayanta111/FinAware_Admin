package org.finAware.project.firebase

import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.tasks.await

object AuthService {
    private val auth = FirebaseAuth.getInstance()

    suspend fun signInWithEmail(email: String, password: String): Boolean {
        return try {
            auth.signInWithEmailAndPassword(email, password).await()
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    fun isUserSignedIn(): Boolean = auth.currentUser != null

    fun getCurrentUserEmail(): String? = auth.currentUser?.email
}
