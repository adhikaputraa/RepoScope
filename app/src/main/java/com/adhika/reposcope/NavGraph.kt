package com.adhika.reposcope

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

@Composable
fun AppNavGraph(navController: NavHostController = rememberNavController()) {
    NavHost(navController, startDestination = "user_list") {
        composable("user_list") {
            UserListScreen { username ->
                navController.navigate("user_detail/$username")
            }
        }
        composable("user_detail/{username}") { backStackEntry ->
            val username = backStackEntry.arguments?.getString("username") ?: return@composable
            UserDetailScreen(username)
        }
    }
}