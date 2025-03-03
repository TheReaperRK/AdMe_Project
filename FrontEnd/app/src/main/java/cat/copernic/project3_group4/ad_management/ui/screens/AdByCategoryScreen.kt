package cat.copernic.project3_group4.ad_management.ui.screens

import android.util.Base64
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.layout.boundsInWindow
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.node.Ref
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.DpOffset
import cat.copernic.project3_group4.R
import cat.copernic.project3_group4.category_management.ui.viewmodels.CategoryViewModel
import cat.copernic.project3_group4.core.models.Category
import cat.copernic.project3_group4.core.models.User
import cat.copernic.project3_group4.core.ui.theme.BrownTertiary
import cat.copernic.project3_group4.core.ui.theme.OrangePrimary
import cat.copernic.project3_group4.core.ui.theme.OrangeSecondary
import cat.copernic.project3_group4.main.screens.BottomNavigationBar
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdsScreen(
    categoryId: Long?,
    adsViewModel: AdsViewModel,
    navController: NavController,
    categoryViewModel: CategoryViewModel,
    userState: MutableState<User?>
) {
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
            Text(stringResource(R.string.no_user))
        }
        return
    }
    val ads by adsViewModel.ads.observeAsState(initial = emptyList())
    val category by categoryViewModel.category.collectAsState(initial =Category())
    var expandedAdminMenu by remember { mutableStateOf(false) }
    var menuPosition by remember { mutableStateOf(Offset.Zero) }
    val density = LocalDensity.current
    var showDialog by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
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
                    .padding(top = 20.dp, start = 0.dp, end = 18.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.Default.KeyboardArrowLeft,
                            contentDescription = stringResource(R.string.back),
                            tint = Color.White
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



                if(user.role.name == "ADMIN"){
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.TopEnd) {
                        //DropdownMenuStyled(navController)
                        Button(
                            onClick = {expandedAdminMenu = !expandedAdminMenu},
                            shape = CircleShape,
                            colors = ButtonDefaults.buttonColors(if(expandedAdminMenu) Color.White else Color.Transparent),
                            modifier = Modifier.onGloballyPositioned { posicion ->
                                val rect = posicion.boundsInWindow()
                                menuPosition = Offset(rect.left, rect.top)
                            }

                        ) {

                            //Text("Modificar")

                            Icon(
                                imageVector = if(expandedAdminMenu) Icons.Default.Close else Icons.Default.Menu ,
                                contentDescription = stringResource(R.string.modify),
                                tint = if(expandedAdminMenu) OrangeSecondary else Color.White, // Color del icono en blanco
                                modifier = Modifier.size(28.dp)
                            )
                        }
                        DropdownMenu(
                            expanded = expandedAdminMenu,
                            onDismissRequest = { expandedAdminMenu = false },
                            offset = with(density) { DpOffset(menuPosition.x.toDp(), menuPosition.y.toDp()-40.dp) },
                            modifier = Modifier
                                .background(MaterialTheme.colorScheme.surface)
                                .border(3.dp, OrangeSecondary, shape = RoundedCornerShape(12.dp))

                        ) {
                            DropdownMenuItem(
                                text = { MenuItemContent("Modificar", Icons.Default.Edit) },
                                onClick = {
                                    expandedAdminMenu = false
                                    navController.navigate("editCategoryScreen/${category.id}")
                                }
                            )
                            DropdownMenuItem(
                                text = { MenuItemContent("Eliminar", Icons.Default.Delete) },
                                onClick = {
                                    expandedAdminMenu = false
                                    showDialog = true
                                }

                            )

                        }

                    }
                }


            }
        }

        if (ads.isEmpty()) {
            Row(modifier = Modifier.fillMaxWidth().wrapContentHeight(), horizontalArrangement = Arrangement.Center) {
                Text("${category.description}", fontSize =20.sp, modifier = Modifier.padding(20.dp))

            }
            HorizontalDivider(modifier = Modifier.padding(horizontal = 40.dp), thickness = 2.dp)
            Text(
                text = stringResource(R.string.no_ads),
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Gray
            )
        } else {
            LazyColumn(modifier = Modifier.weight(1f)) {
                item{
                    Row(modifier = Modifier.fillMaxWidth().wrapContentHeight(), horizontalArrangement = Arrangement.Center) {
                        Text("${category.description}", fontSize =20.sp, modifier = Modifier.padding(20.dp))

                    }
                    HorizontalDivider(modifier = Modifier.padding(horizontal = 40.dp), thickness = 2.dp)
                }
                items(ads) { ad ->
                    AdItem(ad) { clickedCategory ->
                        adsViewModel.setSelectedCategory(clickedCategory)
                    }
                }
            }
        }
        if (showDialog) {
            AlertDialog(
                onDismissRequest = { showDialog = false },
                title = { Text("Confirmar eliminación", fontWeight = FontWeight.Bold, color = BrownTertiary) },
                text = { if(ads.size > 0)
                    Text("La categoria ${category.name} tiene ${ads.size} anuncios asociados, eliminalos para proceder a la eliminacion.")
                    else
                    Text("¿Seguro que quieres eliminar a ${category.name}? Esta acción no se puede deshacer.", color = Color.Black) },

                confirmButton = {
                    Button(
                        onClick = {
                            coroutineScope.launch {
                                try {

                                    val response = categoryViewModel.deleteCategoryById(category.id)

                                    categoryViewModel.fetchProposals()
                                } catch (e: Exception) {
                                    println("Error: ${e.message}")
                                }
                            }

                            showDialog = false
                            navController.navigate("categoryScreen")
                            categoryViewModel.fetchCategories()

                        },
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
                        enabled = if(ads.size > 0) false else true,

                    ) {
                        Text("Eliminar", color = Color.White)
                    }
                },
                dismissButton = {
                    Button(
                        onClick = { showDialog = false },
                        colors = ButtonDefaults.buttonColors(containerColor = Color.LightGray)
                    ) {
                        Text("Cancelar", color = Color.Black)
                    }
                }
            )
        }
        BottomNavigationBar(navController)
    }
}

@Composable
fun AdItem(ad: Ad, onCategoryClick: (Long) -> Unit) {
    var isExpanded by remember { mutableStateOf(false) }
    val imageUrl = remember { base64ToByteArray(ad.data) }
    val author = ad.author

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable { isExpanded = true },
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
                text = stringResource(R.string.category, ad.category.name),
                fontSize = 14.sp,
                color = OrangePrimary,
                modifier = Modifier.clickable { onCategoryClick(ad.category.id) }
            )

            Spacer(modifier = Modifier.height(8.dp))
            Divider(color = OrangeSecondary, thickness = 1.dp)

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
                            .clip(RoundedCornerShape(24.dp)),
                        contentScale = ContentScale.Fit
                    )

                    Spacer(modifier = Modifier.height(8.dp))
                    Text(text = ad.title, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                    Text(text = ad.description, fontSize = 16.sp)
                    Text(text = "${ad.price}€", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                    Text(
                        text = stringResource(R.string.category, ad.category.name),
                        fontSize = 16.sp,
                        color = OrangePrimary,
                        modifier = Modifier.clickable { onCategoryClick(ad.category.id) }
                    )

                    Spacer(modifier = Modifier.height(12.dp))
                    Divider(color = OrangeSecondary, thickness = 1.dp)

                    Text(
                        text = stringResource(R.string.seller_info),
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        modifier = Modifier.padding(vertical = 4.dp)
                    )

                    author?.let {
                        Text(stringResource(R.string.seller_name, it.name), fontSize = 12.sp)
                        Text(stringResource(R.string.seller_email, it.email), fontSize = 12.sp)
                        Text(stringResource(R.string.seller_phone, it.phoneNumber), fontSize = 12.sp)
                    } ?: Text(
                        stringResource(R.string.no_user_info),
                        fontSize = 12.sp,
                        color = Color.Gray
                    )

                    Spacer(modifier = Modifier.height(16.dp))
                    Button(onClick = { isExpanded = false }) {
                        Text(stringResource(R.string.close))
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


@Composable
fun MenuItemContent(text: String, icon: ImageVector) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(icon, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(20.dp))
        Spacer(modifier = Modifier.width(8.dp))
        Text(text, style = MaterialTheme.typography.bodyMedium)
    }
}