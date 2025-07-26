package org.finawreadmin.project

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.google.firebase.FirebaseApp
import org.finawreadmin.project.utils.AppContentHelperImpl
import org.finawreadmin.project.utils.LocalAppContentHelper

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        FirebaseApp.initializeApp(this) // ✅ Required


        setContent {
            // ✅ Provide the AppContentHelperImpl here
            CompositionLocalProvider(
                LocalAppContentHelper provides AppContentHelperImpl()
            ) {
                App()
            }
        }
    }
}

@Preview
@Composable
fun AppAndroidPreview() {
    CompositionLocalProvider(
        LocalAppContentHelper provides AppContentHelperImpl()
    ) {
        App()
    }
}
