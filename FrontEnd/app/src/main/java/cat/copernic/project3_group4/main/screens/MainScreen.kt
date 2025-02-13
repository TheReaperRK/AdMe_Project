package cat.copernic.project3_group4.main.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import cat.copernic.project3_group4.category_management.presentation.CategoryViewModel
import cat.copernic.project3_group4.core.models.Category

@Composable
fun CategoryScreen(viewModel: CategoryViewModel, navController: NavController) {
    val categories by viewModel.categories.observeAsState(emptyList())

    LaunchedEffect(Unit) {
        viewModel.fetchCategories()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        TopBar()
        FilterButtons()
        CategoryList(categories, navController, Modifier.weight(1f))
        BottomNavigationBar(navController)
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBar() {
    TopAppBar(
        title = { Text("Buscar", color = Color.White) },
        colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFFFF6600))
    )
}

@Composable
fun FilterButtons() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        val buttonColor = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFAA00))

        Button(onClick = {}, colors = buttonColor) {
            Text("Filtrar")
        }
        Button(onClick = {}, colors = buttonColor) {
            Text("Todos")
        }
        Button(onClick = {}, colors = buttonColor) {
            Text("Propuesta")
        }
    }
}

@Composable
fun CategoryList(categories: List<Category>, navController: NavController, modifier: Modifier = Modifier) {
    LazyColumn(modifier = modifier.fillMaxWidth()) {
        items(categories) { category ->
            CategoryItem(category, navController)
        }
    }
}

@Composable
fun CategoryItem(category: Category, navController: NavController) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable { navController.navigate("adsScreen/${category.id}") } // Pasar el ID
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(150.dp)
                .background(Color.Gray)
        )
        Text(
            category.name,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(8.dp)
        )
    }
}


@Composable
fun BottomNavigationBar(navController: NavController) {
    val currentRoute = navController.currentDestination?.route

    NavigationBar(containerColor = Color(0xFFFF6600)) {
        NavigationBarItem(
            selected = currentRoute == "categoryScreen",
            onClick = {navController.navigate("categoryScreen")  },
            icon = { /* Icono aquí si lo necesitas */ },
            label = { Text("Inicio") }
        )
        NavigationBarItem(
            selected = currentRoute == "add",
            onClick = {},
            icon = { /* Icono aquí si lo necesitas */ },
            label = { Text("add") }
        )
        NavigationBarItem(
            selected = currentRoute == "profile",
            onClick = {navController.navigate("profile")},
            icon = { /* Icono aquí si lo necesitas */ },
            label = { Text("profile") }
        )
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewCategoryScreen() {
    val viewModel: CategoryViewModel = viewModel()
    val navController = rememberNavController()
    CategoryScreen(viewModel = viewModel, navController = navController)
}
