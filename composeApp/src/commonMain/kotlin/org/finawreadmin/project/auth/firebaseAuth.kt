package org.finawreadmin.project.auth

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

fun loginAsAdmin(
    email: String,
    password: String,
    onSuccess: () -> Unit,
    onError: (Throwable) -> Unit
) {
    val auth = FirebaseAuth.getInstance()
    val db = FirebaseFirestore.getInstance()

    auth.signInWithEmailAndPassword(email, password)
        .addOnSuccessListener { result ->
            val user = result.user
            if (user == null) {
                onError(Exception("Authentication succeeded but user is null"))
                return@addOnSuccessListener
            }

            // Check if user UID exists in the 'admins' collection
            db.collection("admins").document(user.uid).get()
                .addOnSuccessListener { document ->
                    if (document.exists()) {
                        onSuccess()
                    } else {
                        auth.signOut() // optional: sign out unauthorized users
                        onError(Exception("Access denied: Not an authorized admin"))
                    }
                }
                .addOnFailureListener { firestoreError ->
                    onError(firestoreError)
                }
        }
        .addOnFailureListener { authError ->
            onError(authError)
        }
}


