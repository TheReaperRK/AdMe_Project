package cat.copernic.project3_group4.ad_management.ui.screens

import android.util.Base64
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavController
import cat.copernic.project3_group4.ad_management.ui.viewmodels.AdsViewModel
import cat.copernic.project3_group4.core.models.Ad
import cat.copernic.project3_group4.main.screens.BottomNavigationBar
import coil.compose.AsyncImage
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import cat.copernic.project3_group4.category_management.ui.viewmodels.CategoryViewModel
import cat.copernic.project3_group4.core.models.Category
import cat.copernic.project3_group4.core.models.User
import cat.copernic.project3_group4.core.ui.theme.OrangePrimary
import cat.copernic.project3_group4.core.ui.theme.OrangeSecondary
import cat.copernic.project3_group4.main.screens.BottomNavigationBar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdsScreen(categoryId: Long?, adsViewModel: AdsViewModel, navController: NavController, categoryViewModel: CategoryViewModel, userState: MutableState<User?>) {



    LaunchedEffect(categoryId) {
        println("Cargando anuncios para la categoría: $categoryId")
        if (categoryId == null) {
            adsViewModel.fetchAds()
        } else {
            categoryViewModel.fetchCategoryById(categoryId)
            adsViewModel.fetchAdsByCategory(categoryId)
        }
    }
    val user = userState.value
    if (user == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("No hay usuario autenticado")
        }
        return
    }
    val ads by adsViewModel.ads.observeAsState(initial = emptyList())
    val category by categoryViewModel.category.observeAsState(initial =Category())
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        // Contenedor con fondo de color OrangePrimary
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(88.dp)
                .background(OrangeSecondary)
                .padding(horizontal = 8.dp)
        ) {
            Row(

                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = 20.dp, start = 0.dp, end = 18.dp), // Hace que el Row ocupe todo el espacio del Box
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Sección izquierda: Icono + Nombre de la categoría
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = { navController.popBackStack() }
                    ) {
                        Icon(
                            imageVector = Icons.Default.KeyboardArrowLeft,
                            contentDescription = "Volver",
                            tint = Color.White // Color del icono en blanco
                        )
                    }
                    Spacer(modifier = Modifier.width(1.dp))
                    Text(
                        text = "${category.name}",
                        fontSize = 25.sp,
                        fontWeight = FontWeight.W400,
                        color = Color.White
                    )
                }

                // Sección derecha: Número de anuncios
                Text(
                    text = "Anuncios: ${ads.size}",
                    fontSize = 18.sp,
                    color = Color.White,
                    textAlign = TextAlign.End
                )

                if(user.role.name == "ADMIN"){
                    Button(
                        onClick = {navController.navigate("editCategoryScreen/${category.id}")},
                        shape = CircleShape,
                        colors =ButtonDefaults.buttonColors(containerColor = Color(0xFFFFAA00))
                    ) {

                        //Text("Modificar")

                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "Volver",
                            tint = Color.White // Color del icono en blanco
                        )
                    }
                }


            }
        }

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
                        adsViewModel.setSelectedCategory(clickedCategory)
                    }
                }
            }
        }

        BottomNavigationBar(navController)
    }
}

@Composable
fun AdItem(ad: Ad, onCategoryClick: (Long) -> Unit) {
    var isExpanded by remember { mutableStateOf(false) }
    val imageUrl = remember { base64ToByteArray(ad.data) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable {
                isExpanded = true
            },
        shape = RoundedCornerShape(8.dp),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(modifier = Modifier.padding(8.dp)) {
            AsyncImage(
                model = imageUrl,
                contentDescription = null,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp),
                contentScale = ContentScale.Crop
            )

            Text(text = ad.title, fontSize = 18.sp, fontWeight = FontWeight.Bold)
            Text(text = ad.description, fontSize = 14.sp, maxLines = 2, overflow = TextOverflow.Ellipsis)
            Text(text = "${ad.price}€", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.Black)
            Text(
                text = "Categoría: ${ad.category.name}",
                fontSize = 14.sp,
                color = OrangePrimary,
                modifier = Modifier.clickable { onCategoryClick(ad.category.id) }
            )
        }
    }

    if (isExpanded) {
        Dialog(onDismissRequest = { isExpanded = false }) {
            Card(
                shape = RoundedCornerShape(12.dp),
                elevation = CardDefaults.cardElevation(8.dp),
                modifier = Modifier.padding(16.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    AsyncImage(
                        model = imageUrl,
                        contentDescription = null,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp)
                            .clip(RoundedCornerShape(24.dp)), // Bordes redondeados
                        contentScale = ContentScale.Fit
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(text = ad.title, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                    Text(text = ad.description, fontSize = 16.sp)
                    Text(text = "${ad.price}€", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                    Text(
                        text = "Categoría: ${ad.category.name}",
                        fontSize = 16.sp,
                        color = OrangePrimary,
                        modifier = Modifier.clickable { onCategoryClick(ad.category.id) }
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(onClick = { isExpanded = false }) {
                        Text("Cerrar")
                    }
                }
            }
        }
    }
}

fun base64ToByteArray(base64String: String): ByteArray? {
    return try {
        Base64.decode(base64String, Base64.DEFAULT)
    } catch (e: IllegalArgumentException) {
        null
    }
}
