package cat.copernic.project3_group4.main.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.font.FontWeight


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdsScreen(category: String) {
    val ads = listOf(
        Ad("Zapatos y Ropa", "80€", "https://example.com/image1.jpg"),
        Ad("Comida Gourmet", "35€", "https://example.com/image2.jpg")
    )

    Column(modifier = Modifier.fillMaxSize().background(Color.White)) {
        TopAppBar(
            title = { Text(category, color = Color.White) },
            colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFFFF6600))
        )

        LazyColumn(modifier = Modifier.weight(1f)) {
            items(ads) { ad ->
                AdItem(ad)
            }
        }

        BottomNavigationBar() // Asegúrate de que solo haya UNA definición de esta función.
    }
}

data class Ad(val title: String, val price: String, val imageUrl: String)

@Composable
fun AdItem(ad: Ad) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .background(Color(0xFFFFAA00), shape = RoundedCornerShape(8.dp))
    ) {
        AsyncImage(
            model = ad.imageUrl,
            contentDescription = null,
            modifier = Modifier
                .fillMaxWidth()
                .height(150.dp)
        )
        Text(
            ad.title,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(8.dp)
        )
        Text(
            ad.price,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black,
            modifier = Modifier.padding(8.dp)
        )
    }
}

@Composable
fun AsyncImage(model: String, contentDescription: Nothing?, modifier: Modifier) {

}