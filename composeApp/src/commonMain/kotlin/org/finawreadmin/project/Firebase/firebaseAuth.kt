package org.finAware.project.firebase

interface AuthService {
    fun login(email: String, password: String, onResult: (Boolean) -> Unit)
    fun signUp(email: String, password: String, onResult: (Boolean) -> Unit)
    fun logout()
    fun getCurrentUserEmail(): String?
}