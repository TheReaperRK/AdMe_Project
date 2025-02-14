package cat.copernic.project3_group4.main.screens

import android.util.Base64
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.text.font.FontWeight
import androidx.navigation.NavController
import cat.copernic.project3_group4.ad_management.ui.viewmodels.AdsViewModel
import cat.copernic.project3_group4.core.models.Ad
import coil.compose.AsyncImage

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdsScreen(categoryId: Long?, adsViewModel: AdsViewModel, navController: NavController) {
    LaunchedEffect(categoryId) {
        println("Cargando anuncios para la categoría: $categoryId") // Debug
        if (categoryId == null) {
            adsViewModel.fetchAds() // Obtener todos los anuncios
        } else {
            adsViewModel.fetchAdsByCategory(categoryId) // Filtrar por categoría
        }
    }

    val ads by adsViewModel.ads.observeAsState(initial = emptyList())

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        SmallTopAppBar(
            title = { Text(categoryId?.toString() ?: "Todos", color = Color.White) },
            colors = TopAppBarDefaults.smallTopAppBarColors(containerColor = Color(0xFFFF6600))
        )

        if (ads.isEmpty()) {
            Text(
                text = "No hay anuncios disponibles",
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Gray
            )
        } else {
            LazyColumn(modifier = Modifier.weight(1f)) {
                items(ads) { ad ->
                    AdItem(ad) { clickedCategory ->
                        adsViewModel.setSelectedCategory(clickedCategory) // Manejar clic en la categoría
                    }
                }
            }
        }

        BottomNavigationBar(navController)
    }
}


@Composable
fun AdItem(ad: Ad, onCategoryClick: (Long) -> Unit) { // Ahora acepta Long

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .background(Color(0xFFFFAA00), shape = RoundedCornerShape(8.dp))
    ) {
        val imageUrl = remember { "data:image/png;base64,${ad.data}" }

        AsyncImage(
            model = imageUrl,
            contentDescription = null,
            modifier = Modifier
                .fillMaxWidth()
                .height(150.dp)
        )

        Column(modifier = Modifier.padding(8.dp)) {
            Text(
                text = ad.title,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = ad.description,
                fontSize = 14.sp,
                color = Color.Black,
                maxLines = 2
            )

            Text(
                text = "${ad.price}€",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black,
                modifier = Modifier.padding(top = 4.dp)
            )

            // Categoría con clic para filtrar
            Text(
                text = "Categoría: ${ad.category.name}",
                fontSize = 14.sp,
                color = Color.Blue,
                modifier = Modifier
                    .padding(top = 4.dp)
                    .clickable { onCategoryClick(ad.category.id) } // Ahora pasa el ID
            )

        }
    }
}



fun byteArrayToBase64(byteArray: ByteArray?): String {
    return byteArray?.let {
        "data:image/png;base64," + Base64.encodeToString(it, Base64.DEFAULT)
    } ?: ""
}

