package org.finawreadmin.project.Firebase
import android.app.Activity
import com.google.firebase.auth.FirebaseAuth

class AuthServiceImpl(private val activity: Activity) : AuthService {
    private val firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()

    override fun login(email: String, password: String, onResult: (Boolean) -> Unit) {
        firebaseAuth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(activity) { task ->
                onResult(task.isSuccessful)
            }
    }

    override fun signUp(email: String, password: String, onResult: (Boolean) -> Unit) {
        firebaseAuth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(activity) { task ->
                onResult(task.isSuccessful)
            }
    }

    override fun logout() {
        firebaseAuth.signOut()
    }

    override fun getCurrentUserEmail(): String? {
        return firebaseAuth.currentUser?.email
    }
}