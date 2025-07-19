package org.finawreadmin.project.utils

import android.content.Context
import android.content.res.Configuration
import android.net.Uri
import android.os.Build
import androidx.compose.runtime.staticCompositionLocalOf
import com.google.firebase.Firebase
import com.google.firebase.storage.storage
import kotlinx.coroutines.tasks.await
import java.util.Locale
import java.util.UUID

interface AppContentHelper {
    fun setLocale(context: Context, localeCode: String)
    suspend fun uploadImageToFirebase(uri: Uri): String?
}

class AppContentHelperImpl : AppContentHelper {

    override fun setLocale(context: Context, localeCode: String) {
        val locale = Locale(localeCode)
        Locale.setDefault(locale)

        val config = Configuration(context.resources.configuration)
        config.setLocale(locale)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            context.createConfigurationContext(config)
        } else {
            @Suppress("DEPRECATION")
            context.resources.updateConfiguration(config, context.resources.displayMetrics)
        }
    }

    override suspend fun uploadImageToFirebase(uri: Uri): String? {
        return try {
            val storage = Firebase.storage
            val imageRef = storage.reference.child("learning_images/${UUID.randomUUID()}.jpg")
            imageRef.putFile(uri).await()
            imageRef.downloadUrl.await().toString()
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}

// CompositionLocal for injection into Composables
val LocalAppContentHelper = staticCompositionLocalOf<AppContentHelper> {
    error("No AppContentHelper provided")
}
