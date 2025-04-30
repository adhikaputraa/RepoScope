package com.adhika.reposcope

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.adhika.reposcope.presentation.favorites.FavoriteReposScreen
import com.adhika.reposcope.presentation.userdetail.UserDetailScreen
import com.adhika.reposcope.presentation.userlist.UserListScreen

@Composable
fun AppNavGraph(navController: NavHostController = rememberNavController()) {
    NavHost(navController, startDestination = "user_list") {
        composable("user_list") {
            UserListScreen(
                onUserClick = { username, avatarUrl ->
                    navController.navigate("user_detail/$username?avatarUrl=$avatarUrl")
                },
                onFavoriteClick = { navController.navigate("favorite") }
            )
        }
        composable("user_detail/{username}?avatarUrl={avatarUrl}") { backStackEntry ->
            val username = backStackEntry.arguments?.getString("username") ?: return@composable
            val avatarUrl = backStackEntry.arguments?.getString("avatarUrl")
            UserDetailScreen( username = username, avatarUrl = avatarUrl, onBackClick = { navController.popBackStack() } )
        }
        composable("favorite") {
            FavoriteReposScreen()
        }
    }
}