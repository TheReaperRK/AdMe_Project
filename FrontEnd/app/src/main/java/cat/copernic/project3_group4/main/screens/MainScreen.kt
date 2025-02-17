package cat.copernic.project3_group4.main.screens

import android.graphics.BitmapFactory
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import cat.copernic.project3_group4.ad_management.ui.screens.base64ToByteArray
import cat.copernic.project3_group4.category_management.ui.viewmodels.CategoryViewModel
import cat.copernic.project3_group4.core.models.Category
import coil.compose.AsyncImage

@Composable
fun CategoryScreen(viewModel: CategoryViewModel, navController: NavController) {
    val categories by viewModel.categories.observeAsState(emptyList())
    var searchText by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        viewModel.fetchCategories()
    }

    // Filtra las categorías en función del texto ingresado
    val filteredCategories by remember(searchText, categories) {
        derivedStateOf {
            categories.filter { it.name.startsWith(searchText, ignoreCase = true) }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        TopBar(searchText) { searchText = it }
        FilterButtons(navController)
        CategoryList(filteredCategories, navController, Modifier.weight(1f))
        BottomNavigationBar(navController)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBar(searchText: String, onSearchTextChange: (String) -> Unit) {
    Column {
        TopAppBar(
            title = { Text("Buscar", color = Color.White) },
            colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFFFF6600))
        )
        TextField(
            value = searchText,
            onValueChange = onSearchTextChange,
            placeholder = { Text("Buscar categoría...") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            singleLine = true
        )
    }
}


@Composable
fun FilterButtons(navController: NavController) {
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
        Button(onClick = {navController.navigate("categoryFormScreen")}, colors = buttonColor, ) {
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
    val imageUrl = remember { base64ToByteArray(category.image) }
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable { navController.navigate("adsScreen/${category.id}") } // Pasar el ID
    ) {
        AsyncImage(
            model = imageUrl,
            contentDescription = null,
            modifier = Modifier
                .fillMaxWidth()
                .height(150.dp)
                .clip(RoundedCornerShape(12.dp)), // Bordes redondeados
            contentScale = ContentScale.Crop
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
            selected = currentRoute == "AdsScreen",
            onClick = {navController.navigate("AdsScreen")},
            icon = { /* Icono aquí si lo necesitas */ },
            label = { Text("ads") }
        )
        NavigationBarItem(
            selected = currentRoute == "createAdScreen",
            onClick = {navController.navigate("createAdScreen")},
            icon = { /* Icono aquí si lo necesitas */ },
            label = { Text("create") }
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
