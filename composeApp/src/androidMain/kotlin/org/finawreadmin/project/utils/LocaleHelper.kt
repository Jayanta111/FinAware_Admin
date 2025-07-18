// app/src/main/java/org/finawreadmin/project/utils/AppContentHelper.kt (or similar location)
package org.finawreadmin.project.utils

import android.content.Context
import android.net.Uri
import com.google.firebase.Firebase
import com.google.firebase.storage.storage
import kotlinx.coroutines.tasks.await
import java.util.UUID

interface AppContentHelper {
    fun setLocale(context: Context, localeCode: String)
    suspend fun uploadImageToFirebase(uri: Uri): String?
    // Add other context-dependent functions here if needed
}

class AppContentHelperImpl : AppContentHelper {
    override fun setLocale(context: Context, localeCode: String) {
        setAppLocale(context, localeCode) // Your existing utility function
    }

    override suspend fun uploadImageToFirebase(uri: Uri): String? {
        val storage = Firebase.storage
        val imageRef = storage.reference.child("learning_images/${UUID.randomUUID()}.jpg")
        val uploadTask = imageRef.putFile(uri).await()
        return if (uploadTask.task.isSuccessful) {
            imageRef.downloadUrl.await().toString()
        } else null
    }
}

val LocalAppContentHelper = staticCompositionLocalOf<AppContentHelper> {
    error("No AppContentHelper provided")
}