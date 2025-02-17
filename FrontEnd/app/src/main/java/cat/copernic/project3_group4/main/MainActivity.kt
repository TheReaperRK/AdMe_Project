package cat.copernic.project3_group4.main

import cat.copernic.project3_group4.user_management.ui.screens.LoginScreen
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding

import androidx.compose.material3.Scaffold
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import cat.copernic.project3_group4.core.ui.theme.Project3_Group4Theme
import androidx.navigation.NavHostController
import cat.copernic.project3_group4.core.models.User
import cat.copernic.project3_group4.user_management.ui.screens.RegisterScreen
import cat.copernic.project3_group4.user_management.ui.screens.UserListScreen

import cat.copernic.project3_group4.ad_management.ui.screens.AdsScreen
import cat.copernic.project3_group4.main.screens.CategoryScreen
import cat.copernic.project3_group4.category_management.ui.viewmodels.CategoryViewModel
import cat.copernic.project3_group4.ad_management.ui.viewmodels.AdsViewModel
import cat.copernic.project3_group4.ad_management.ui.screens.CreateAdScreen
import cat.copernic.project3_group4.main.screens.PasswordRecover
import cat.copernic.project3_group4.main.screens.ProfileScreen
import cat.copernic.project3_group4.main.screens.RecoverByToken

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Project3_Group4Theme {
                val navController: NavHostController = rememberNavController()
                val categoryViewModel: CategoryViewModel = viewModel()
                val adsViewModel: AdsViewModel = viewModel()
                // Aquí guardamos el usuario autenticado
                val userState = rememberSaveable { mutableStateOf<User?>(null) }

                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    NavHost(
                        navController = navController,
                        startDestination = "login",
                        modifier = Modifier.padding(innerPadding)
                    ) {
                        composable("login") { LoginScreen(navController, userState) }
                        composable("user_list") { UserListScreen(navController) }
                        composable("profile") { ProfileScreen(userState, navController) }
                        composable("register") { RegisterScreen(navController) }
                        composable("categoryScreen") {
                            CategoryScreen(
                                viewModel = categoryViewModel,
                                navController = navController
                            )
                        }
                        composable("paswordRecover") { PasswordRecover(navController) }
                        composable(
                            route = "adsScreen/{categoryId}",
                            arguments = listOf(navArgument("categoryId") {
                                type = NavType.LongType
                            })
                        ) { backStackEntry ->
                            val categoryId = backStackEntry.arguments?.getLong("categoryId") ?: 0L
                            AdsScreen(
                                categoryId,
                                adsViewModel,
                                navController
                            ) // Se pasa como Long en lugar de String
                        }
                        composable("createAdScreen") { CreateAdScreen(navController, viewModel = categoryViewModel, userState) }
                        composable("recoverByToken") { RecoverByToken(navController) }
                        composable("AdsScreen") {
                            AdsScreen(adsViewModel, navController)
                        }

                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewMainScreen() {
    val navController = rememberNavController()
    val categoryViewModel: CategoryViewModel = viewModel()
    val adsViewModel: AdsViewModel = viewModel()

    Project3_Group4Theme {
        CategoryScreen(viewModel = categoryViewModel, navController = navController)
    }
}