package com.example.cartoongenerator.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.cartoongenerator.ui.screens.home.HomeScreen
import com.example.cartoongenerator.ui.screens.editor.EditorScreen
import com.example.cartoongenerator.ui.screens.gallery.GalleryScreen

sealed class Screen(val route: String) {
    object Home : Screen("home")
    object Editor : Screen("editor")
    object Gallery : Screen("gallery")
}

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Screen.Home.route
    ) {
        composable(Screen.Home.route) {
            HomeScreen(
                onNavigateToEditor = { navController.navigate(Screen.Editor.route) },
                onNavigateToGallery = { navController.navigate(Screen.Gallery.route) }
            )
        }
        composable(Screen.Editor.route) {
            EditorScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }
        composable(Screen.Gallery.route) {
            GalleryScreen(
                onNavigateBack = { navController.popBackStack() },
                onNavigateToEditor = { navController.navigate(Screen.Editor.route) }
            )
        }
    }
}