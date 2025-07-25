package org.finawreadmin.project.Ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import org.finawreadmin.project.API.client
import org.finawreadmin.project.BuildConfig
import org.finawreadmin.project.model.ContentGeneratorAI

@Composable
fun AdminNavigation(navController: NavHostController, modifier: Modifier = Modifier) {
    NavHost(
        navController = navController,
        startDestination = "dashboard",
        modifier = modifier
    ) {
        composable("dashboard") { DashboardScreen() }
        composable("content") { ContentManagementScreen(navController,client) }
        composable("user") { UserManagementScreen() }

        // ✅ Manual content creation screen
        composable(
            route = "contentGenerator/{courseId}",
            arguments = listOf(navArgument("courseId") { type = NavType.StringType })
        ) { backStackEntry ->
            val courseId = backStackEntry.arguments?.getString("courseId") ?: ""
            ContentGeneratorScreen(navController, courseId)
        }

        // ✅ AI-powered content creation screen with Gemini API
        composable(
            route = "ContentGeneratorAI/{courseId}",
            arguments = listOf(navArgument("courseId") { type = NavType.StringType })
        ) { backStackEntry ->
            val courseId = backStackEntry.arguments?.getString("courseId") ?: ""
            val apiKey = BuildConfig.GEMINI_API_KEY

            ContentGeneratorAIScreen(
                navController = navController,
                courseId = courseId,
                apiKey = apiKey
            )
        }

        // ✅ Quiz builder screen
        composable("quiz_builder/{courseId}/{title}") { backStackEntry ->
            val courseId = backStackEntry.arguments?.getString("courseId") ?: ""
            val title = backStackEntry.arguments?.getString("title") ?: ""
            QuizBuilderScreen(courseId, title, navController)
        }
        }
    }

