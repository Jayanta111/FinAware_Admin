package org.finawreadmin.project.auth

import com.google.firebase.auth.FirebaseAuth

fun loginAsAdmin(
    email: String,
    password: String,
    onSuccess: () -> Unit,
    onError: (Throwable) -> Unit
) {
    val auth = FirebaseAuth.getInstance()

    auth.signInWithEmailAndPassword(email, password)
        .addOnSuccessListener { result ->
            val user = result.user
            if (user != null) {
                onSuccess()
            } else {
                onError(Exception("Login failed: User not found"))
            }
        }
        .addOnFailureListener { exception ->
            onError(exception)
        }
}
