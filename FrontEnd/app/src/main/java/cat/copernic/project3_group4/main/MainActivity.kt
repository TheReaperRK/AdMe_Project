package cat.copernic.project3_group4.main

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
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
import cat.copernic.project3_group4.main.screens.AdsScreen
import cat.copernic.project3_group4.main.screens.CategoryScreen
import cat.copernic.project3_group4.category_management.presentation.CategoryViewModel
import cat.copernic.project3_group4.ad_management.presentation.AdsViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Project3_Group4Theme {
                val navController = rememberNavController()
                val categoryViewModel: CategoryViewModel = viewModel()
                val adsViewModel: AdsViewModel = viewModel()

                Scaffold(modifier = Modifier.fillMaxSize()) {
                    NavHost(navController, startDestination = "categoryScreen") {
                        // Pantalla de categorías
                        composable("categoryScreen") {
                            CategoryScreen(viewModel = categoryViewModel, navController = navController)
                        }

                        // Pantalla de anuncios por categoría (Usamos el ID como parámetro)
                        composable(
                            route = "adsScreen/{categoryId}",
                            arguments = listOf(navArgument("categoryId") { type = NavType.LongType })
                        ) { backStackEntry ->
                            val categoryId = backStackEntry.arguments?.getLong("categoryId") ?: 0L
                            AdsScreen(categoryId, adsViewModel, navController) // Se pasa como Long en lugar de String
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