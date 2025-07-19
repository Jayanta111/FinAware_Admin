package org.finawreadmin.project.firebase

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import org.finAware.project.firebase.AuthService

class AuthViewModel(private val authService: AuthService) : ViewModel() {

    fun login(email: String, password: String, onResult: (Boolean) -> Unit) {
        authService.login(email, password, onResult)
    }

    fun signUp(email: String, password: String, onResult: (Boolean) -> Unit) {
        authService.signUp(email, password, onResult)
    }

    fun logout() {
        authService.logout()
    }

    fun getCurrentUserEmail(): String? {
        return authService.getCurrentUserEmail()
    }

    @Suppress("UNCHECKED_CAST")
    class Factory(private val service: AuthService) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(AuthViewModel::class.java)) {
                return AuthViewModel(service) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}
