package cat.copernic.project3_group4.main.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewmodel.compose.viewModel


import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import cat.copernic.project3_group4.category_management.presentation.CategoryViewModel
import cat.copernic.project3_group4.core.models.Category

@Composable
fun CategoryScreen(viewModel: CategoryViewModel = viewModel()) {
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
        // Agregar weight(1f) para que la lista ocupe el espacio restante y permita el scroll
        CategoryList(categories, Modifier.weight(1f))
        BottomNavigationBar()
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
fun CategoryList(categories: List<Category>, modifier: Modifier = Modifier) {
    LazyColumn(modifier = modifier.fillMaxWidth()) {
        items(categories) { category ->
            CategoryItem(category.name)
        }
    }
}

@Composable
fun CategoryItem(title: String) {
    Column(modifier = Modifier.fillMaxWidth().padding(8.dp)) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(150.dp)
                .background(Color.Gray)
        )
        Text(
            title,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(8.dp)
        )
    }
}

@Composable
fun BottomNavigationBar() {
    NavigationBar(containerColor = Color(0xFFFF6600)) {
        NavigationBarItem(
            selected = true,
            onClick = {},
            icon = { /* Icono aquí si lo necesitas */ },
            label = { Text("Inicio") }
        )
        NavigationBarItem(
            selected = false,
            onClick = {},
            icon = { /* Icono aquí si lo necesitas */ },
            label = { Text("Agregar") }
        )
        NavigationBarItem(
            selected = false,
            onClick = {},
            icon = { /* Icono aquí si lo necesitas */ },
            label = { Text("Perfil") }
        )
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewCategoryScreen() {
    CategoryScreen()
}