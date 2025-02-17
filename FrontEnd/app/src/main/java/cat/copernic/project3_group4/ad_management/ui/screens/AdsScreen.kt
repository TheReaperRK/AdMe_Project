package cat.copernic.project3_group4.ad_management.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import cat.copernic.project3_group4.ad_management.ui.viewmodels.AdsViewModel
import cat.copernic.project3_group4.core.models.Ad
import cat.copernic.project3_group4.core.models.Category
import coil.compose.AsyncImage
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import cat.copernic.project3_group4.main.screens.BottomNavigationBar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdsScreen(adsViewModel: AdsViewModel, navController: NavController) {
    var selectedCategories by remember { mutableStateOf(setOf<Long>()) }
    var maxPrice by remember { mutableStateOf(10000f) }
    val ads by adsViewModel.ads.observeAsState(initial = emptyList())
    val categories by adsViewModel.categories.observeAsState(initial = emptyList())

    LaunchedEffect(Unit) {
        adsViewModel.fetchCategories()
    }

    LaunchedEffect(selectedCategories, maxPrice) {
        adsViewModel.fetchFilteredAds(
            selectedCategories, // Ahora pasamos todas las categorías seleccionadas
            0.0,
            maxPrice.toDouble()
        )
    }


    Column(modifier = Modifier.fillMaxSize().background(Color.White)) {
        SmallTopAppBar(
            title = { Text("Anuncios", color = Color.White) },
            colors = TopAppBarDefaults.smallTopAppBarColors(containerColor = Color(0xFFFF6600))
        )

        FilterSection(categories, selectedCategories, maxPrice, onCategorySelected = {
            selectedCategories = it
        }, onPriceChanged = {
            maxPrice = it
        })

        Spacer(modifier = Modifier.height(8.dp))

        if (ads.isEmpty()) {
            Text(
                "No hay anuncios disponibles", fontSize = 16.sp,
                color = Color.Gray, modifier = Modifier.padding(16.dp)
            )
        } else {
            LazyColumn(modifier = Modifier.weight(1f).padding(horizontal = 8.dp)) {
                items(ads) { ad -> AdItem(ad) }
            }
        }

        Spacer(modifier = Modifier.weight(1f)) // Empuja el contenido hacia arriba para que el BottomNavigationBar esté siempre abajo
        BottomNavigationBar(navController)
    }
}
@Composable
fun FilterSection(
    categories: List<Category>,
    selectedCategories: Set<Long>,
    maxPrice: Float,
    onCategorySelected: (Set<Long>) -> Unit,
    onPriceChanged: (Float) -> Unit
) {
    Column(modifier = Modifier.padding(16.dp)) {
        Text("Filtrar por Categoría", fontSize = 16.sp, fontWeight = FontWeight.Bold)

        Column {
            var rowElements = mutableListOf<Category>()
            categories.forEachIndexed { index, category ->
                rowElements.add(category)
                if (rowElements.size == 3 || index == categories.lastIndex) { // Máximo 3 por fila
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        rowElements.forEach { cat ->
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Checkbox(
                                    checked = selectedCategories.contains(cat.id),
                                    onCheckedChange = {
                                        val newSelection = selectedCategories.toMutableSet()
                                        if (it) newSelection.add(cat.id) else newSelection.remove(cat.id)
                                        onCategorySelected(newSelection)
                                    }
                                )
                                Text(cat.name)
                            }
                        }
                    }
                    rowElements = mutableListOf()
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
        Text("Filtrar por Precio: 0€ - ${maxPrice.toInt()}€", fontWeight = FontWeight.Bold)
        Slider(
            value = maxPrice,
            onValueChange = onPriceChanged,
            valueRange = 0f..10000f
        )
    }
}


@Composable
fun AdItem(ad: Ad) {
    val imageUrl = remember { base64ToByteArray(ad.data) }
    Card(
        modifier = Modifier.fillMaxWidth().padding(8.dp),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFFFDD80))
    ) {
        Column {
            AsyncImage(
                model = imageUrl,
                contentDescription = null,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp)
                    .clip(RoundedCornerShape(12.dp)), // Bordes redondeados
                contentScale = ContentScale.Crop
            )
            Column(modifier = Modifier.padding(8.dp)) {
                Text(ad.title, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                Text(ad.description, fontSize = 14.sp, maxLines = 2)
                Text("${ad.price}€", fontSize = 14.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}