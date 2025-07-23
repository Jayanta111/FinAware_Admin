package org.finawreadmin.project.Ui

import androidx.navigation.NavController
import androidx.compose.runtime.Composable

@Composable
expect fun ContentGeneratorScreen(navController: NavController, courseId: String)
