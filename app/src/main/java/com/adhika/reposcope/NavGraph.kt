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
            UserListScreen { username, avatarUrl ->
                navController.navigate("user_detail/$username?avatarUrl=$avatarUrl")
            }
        }
        composable("user_detail/{username}?avatarUrl={avatarUrl}") { backStackEntry ->
            val username = backStackEntry.arguments?.getString("username") ?: return@composable
            val avatarUrl = backStackEntry.arguments?.getString("avatarUrl")
            UserDetailScreen( username = username, avatarUrl = avatarUrl, onBackClick = { navController.popBackStack() } )
        }
    }
}