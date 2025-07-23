package org.finawreadmin.project.Ui

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController

@Composable
expect fun ContentGeneratorAIScreen(
    navController: NavHostController,
    courseId: String,
    apiKey: String
)
