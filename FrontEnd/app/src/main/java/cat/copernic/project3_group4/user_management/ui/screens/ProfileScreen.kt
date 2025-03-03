package cat.copernic.project3_group4.main.screens

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import cat.copernic.project3_group4.R
import cat.copernic.project3_group4.core.models.User
import cat.copernic.project3_group4.ad_management.ui.viewmodels.AdsViewModel
import cat.copernic.project3_group4.core.models.Ad
import cat.copernic.project3_group4.core.ui.theme.BrownTertiary
import cat.copernic.project3_group4.core.ui.theme.OrangePrimary
import cat.copernic.project3_group4.core.ui.theme.OrangeSecondary
import coil.compose.AsyncImage
import kotlinx.coroutines.launch
import cat.copernic.project3_group4.main.screens.BottomNavigationBar
import cat.copernic.project3_group4.user_management.ui.viewmodels.ProfileViewModel
import com.google.android.gms.common.api.Response

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(userState: MutableState<User?>, navController: NavController, adsViewModel: AdsViewModel = viewModel(), profileViewModel: ProfileViewModel) {

    var selectedImageUri by remember { mutableStateOf<android.net.Uri?>(null) }
    val context = LocalContext.current
    val user = userState.value
    val scope = rememberCoroutineScope()
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    var showChangeImageDialog by remember { mutableStateOf(false)
    }

    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        selectedImageUri = uri!! // ✅ Guarda la URI seleccionada en una variable de estado
        profileViewModel.uploadProfileImage(user!!.id ,uri, context) // ✅ Ahora sí podemos usarla aquí
    }

    if (user == null) {
        Text(stringResource(R.string.no_user_authenticated), textAlign = TextAlign.Center, modifier = Modifier.fillMaxSize())
        return
    }

    LaunchedEffect(user.id) {
        adsViewModel.fetchAdsByUser(user.id.toString())
    }

    val ads by adsViewModel.ads.observeAsState(initial = emptyList())
    val imageBitmap = user.imageBytes?.let { BitmapFactory.decodeByteArray(it, 0, it.size) }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet {
                Column(
                    modifier = Modifier.fillMaxSize().padding(16.dp),
                    verticalArrangement = Arrangement.Top
                ) {
                    Text(stringResource(R.string.menu), fontSize = 22.sp, fontWeight = FontWeight.Bold, color = OrangePrimary)
                    Spacer(modifier = Modifier.height(16.dp))

                    if(user.role.name == "ADMIN") {
                        DrawerButton(stringResource(R.string.users)) { navController.navigate("user_list") }
                        DrawerButton(stringResource(R.string.proposals)) { navController.navigate("proposal_list") }
                    }
                    DrawerButton(stringResource(R.string.logout)) {
                        userState.value = null
                        navController.navigate("login")
                    }
                    DrawerButton(stringResource(R.string.reset_password)) {
                        navController.navigate("passwordRecover")
                    }
                }
            }
        }
    ) {
        Scaffold(
            topBar = {
                SmallTopAppBar(
                    title = { Text(stringResource(R.string.profile), color = Color.White) },
                    colors = TopAppBarDefaults.smallTopAppBarColors(containerColor = OrangeSecondary),
                    navigationIcon = {
                        IconButton(onClick = { scope.launch { drawerState.open() } }) {
                            Icon(imageVector = Icons.Default.Menu, contentDescription = stringResource(R.string.menu), tint = Color.White)
                        }
                    }
                )
            },
            bottomBar = {
                BottomNavigationBar(navController)
            }
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.White)
                    .padding(paddingValues),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(20.dp))
                imageBitmap?.let {
                    Image(
                        bitmap = it.asImageBitmap(),
                        contentDescription = stringResource(R.string.profile_image),
                        modifier = Modifier
                            .size(100.dp)
                            .clip(CircleShape)
                            .clickable { showChangeImageDialog = true },
                        contentScale = ContentScale.Crop
                    )
                }
                Spacer(modifier = Modifier.height(12.dp))

                if (showChangeImageDialog) {
                    ChangeImageDialog(
                        onDismiss = { showChangeImageDialog = false },
                        onConfirm = { launcher.launch("image/*") }
                    )
                }
                Spacer(modifier = Modifier.height(12.dp))
                Text(user.name, fontSize = 22.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                Spacer(modifier = Modifier.height(6.dp))
                Text(stringResource(R.string.email, user.email), fontSize = 14.sp, color = Color.Gray)
                Text(stringResource(R.string.phone, user.phoneNumber), fontSize = 14.sp, color = Color.Gray)
                Text(stringResource(R.string.status, if (user.isStatus) stringResource(R.string.active) else stringResource(R.string.inactive)), fontSize = 14.sp, color = Color.Gray)
                Text(stringResource(R.string.role, user.role), fontSize = 14.sp, color = Color.Gray)
                Spacer(modifier = Modifier.height(24.dp))
                Text(stringResource(R.string.my_ads), fontSize = 20.sp, fontWeight = FontWeight.Bold, color = OrangePrimary)

                if (ads.isEmpty()) {
                    Text(stringResource(R.string.no_ads), fontSize = 14.sp, color = Color.Gray)
                } else {
                    AdsSection(ads, adsViewModel, navController)
                }
            }
        }
    }
}
@Composable
fun AdsSection(ads: List<Ad>, adsViewModel: AdsViewModel, navController: NavController) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(8.dp)
    ) {
        items(ads) { ad ->
            AdCard(ad, adsViewModel, navController) // ✅ Pasamos navController a cada AdCard
        }
    }
}


@Composable
fun AdCard(ad: Ad, adsViewModel: AdsViewModel, navController: NavController) {
    val context = LocalContext.current
    val isBase64 = ad.data?.matches(Regex("^[A-Za-z0-9+/=]+$")) ?: false
    val decodedBitmap = if (isBase64) base64ToBitmap(ad.data) else null
    var showMenu by remember { mutableStateOf(false) }
    var showDialog by remember { mutableStateOf(false) }

    Box {
        Card(
            modifier = Modifier
                .padding(8.dp)
                .fillMaxWidth()
                .clickable { showMenu = true },
            shape = RoundedCornerShape(8.dp),
            elevation = CardDefaults.cardElevation(4.dp)
        ) {
            Column(modifier = Modifier.background(Color.White)) {
                if (decodedBitmap != null) {
                    Image(
                        bitmap = decodedBitmap.asImageBitmap(),
                        contentDescription = stringResource(R.string.ad_image),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(120.dp),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    AsyncImage(
                        model = "https://via.placeholder.com/150",
                        contentDescription = stringResource(R.string.ad_image),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(120.dp),
                        contentScale = ContentScale.Fit
                    )
                }
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(OrangePrimary)
                        .padding(4.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = stringResource(R.string.price, ad.price.toString()),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }
        }

        DropdownMenu(
            expanded = showMenu,
            onDismissRequest = { showMenu = false }
        ) {
            DropdownMenuItem(
                text = { Text(stringResource(R.string.edit)) },
                onClick = {
                    showMenu = false
                    navController.navigate("UpdateAdScreen/${ad.id}")
                }
            )
            DropdownMenuItem(
                text = { Text(stringResource(R.string.delete)) },
                onClick = {
                    showMenu = false
                    showDialog = true
                }
            )
        }

        if (showDialog) {
            AlertDialog(
                onDismissRequest = { showDialog = false },
                title = {
                    Text(
                        stringResource(R.string.confirm_deletion),
                        fontWeight = FontWeight.Bold,
                        color = BrownTertiary
                    )
                },
                text = {
                    Text(
                        stringResource(R.string.confirm_deletion_message),
                        color = Color.Black
                    )
                },
                confirmButton = {
                    Button(
                        onClick = {
                            adsViewModel.deleteAd(ad.id,
                                onSuccess = {
                                    Toast.makeText(
                                        context,
                                        context.getString(R.string.ad_deleted),
                                        Toast.LENGTH_SHORT
                                    ).show()
                                },
                                onError = { errorMessage ->
                                    Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT)
                                        .show()
                                }
                            )
                            showDialog = false
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                    ) {
                        Text(stringResource(R.string.delete), color = Color.White)
                    }
                },
                dismissButton = {
                    Button(
                        onClick = { showDialog = false },
                        colors = ButtonDefaults.buttonColors(containerColor = Color.LightGray)
                    ) {
                        Text(stringResource(R.string.cancel), color = Color.Black)
                    }
                }
            )
        }
    }
}


fun base64ToBitmap(base64Str: String?): Bitmap? {
    return try {
        if (base64Str.isNullOrEmpty()) return null
        val decodedBytes = Base64.decode(base64Str, Base64.DEFAULT)
        BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.size)
    } catch (e: Exception) {
        null
    }
}

@Composable
fun DrawerButton(text: String, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(containerColor = OrangePrimary),
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp)
    ) {
        Text(text, color = Color.White)
    }
}

@Composable
fun ChangeImageDialog(onDismiss: () -> Unit, onConfirm: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Cambiar imagen de perfil") },
        text = { Text("¿Deseas cambiar tu imagen de perfil?") },
        confirmButton = {
            Button(onClick = onConfirm) {
                Text("Seleccionar imagen")
            }
        },
        dismissButton = {
            Button(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}
