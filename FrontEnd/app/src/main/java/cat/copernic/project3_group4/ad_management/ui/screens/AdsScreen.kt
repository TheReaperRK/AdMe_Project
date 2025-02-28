package cat.copernic.project3_group4.ad_management.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
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
import cat.copernic.project3_group4.core.ui.theme.OrangePrimary
import cat.copernic.project3_group4.core.ui.theme.OrangeSecondary
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


    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
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

        // Aquí envolvemos la lista en un Box con weight(1f)
        Box(modifier = Modifier.weight(1f)) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(ads) { ad -> AdItem(ad) }
            }
        }

        // La barra de navegación siempre quedará en la parte inferior
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

        LazyRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(categories) { category ->
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(4.dp)
                ) {
                    Checkbox(
                        checked = selectedCategories.contains(category.id),
                        onCheckedChange = {
                            val newSelection = selectedCategories.toMutableSet()
                            if (it) newSelection.add(category.id) else newSelection.remove(category.id)
                            onCategorySelected(newSelection)
                        }
                    )
                    Text(category.name)
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
    val author = ad.author // Accede al autor del anuncio

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = OrangePrimary)
    ) {
        Column {
            AsyncImage(
                model = imageUrl,
                contentDescription = null,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp)
                    .clip(RoundedCornerShape(0.dp, 0.dp, 24.dp, 24.dp)), // Bordes redondeados
                contentScale = ContentScale.Crop
            )
            Column(modifier = Modifier.padding(8.dp)) {
                Text(ad.title, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                Text(ad.description, fontSize = 14.sp, maxLines = 2)
                Text("${ad.price}€", fontSize = 14.sp, fontWeight = FontWeight.Bold)

                Spacer(modifier = Modifier.height(8.dp))

                Divider(color = Color.Black, thickness = 1.dp)

                Text(
                    text = "Información del vendedor:",
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    modifier = Modifier.padding(vertical = 4.dp)
                )

                author?.let {
                    Text("Nombre: ${it.name}", fontSize = 12.sp)
                    Text("Email: ${it.email}", fontSize = 12.sp)
                    Text("Teléfono: ${it.phoneNumber}", fontSize = 12.sp)
                } ?: Text(
                    "No se encontró información del usuario",
                    fontSize = 12.sp,
                    color = Color.Gray
                )
            }
        }
    }
}

