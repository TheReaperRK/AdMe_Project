package cat.copernic.project3_group4.ad_management.ui.screens

import android.app.Activity
import android.net.Uri
import android.util.Base64
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import cat.copernic.project3_group4.ad_management.ui.viewmodels.AdsViewModel
import cat.copernic.project3_group4.category_management.ui.viewmodels.CategoryViewModel
import cat.copernic.project3_group4.core.models.Ad
import cat.copernic.project3_group4.core.models.Category
import cat.copernic.project3_group4.core.models.User
import cat.copernic.project3_group4.main.screens.BottomNavigationBar
import kotlinx.coroutines.launch
import java.io.InputStream



@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UpdateAdScreen(
    navController: NavController,
    adsViewModel: AdsViewModel,
    viewModel: CategoryViewModel,
    ad: Ad,
    userState: MutableState<User?>
) {
    val user = userState.value
    var title by remember { mutableStateOf(ad.title) }
    var description by remember { mutableStateOf(ad.description) }
    var price by remember { mutableStateOf(ad.price.toString()) }
    var selectedCategory by remember { mutableStateOf(ad.category) }
    var imageUri by remember { mutableStateOf<Uri?>(null) }
    var encodedImage by remember { mutableStateOf(ad.data ?: "") }
    val context = LocalContext.current
    val categories by viewModel.categories.collectAsState(initial = emptyList())
    val coroutineScope = rememberCoroutineScope()

    val imagePickerLauncher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let { selectedUri ->
            imageUri = selectedUri
            encodedImage = encodeImage(context.contentResolver.openInputStream(selectedUri))
        }
    }

    Scaffold(
        bottomBar = { BottomNavigationBar(navController) }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(Color.White)
        ) {
            SmallTopAppBar(
                title = { Text("Actualizar Anuncio", color = Color.White) },
                colors = TopAppBarDefaults.smallTopAppBarColors(containerColor = Color(0xFFFF6600))
            )

            Column(modifier = Modifier.padding(16.dp)) {
                Text("Título", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                OutlinedTextField(
                    value = title,
                    onValueChange = { if (it.length in 0..15) title = it },
                    modifier = Modifier.fillMaxWidth(),
                    textStyle = TextStyle(fontSize = 16.sp),
                    placeholder = { Text("Introduce entre 4 y 15 caracteres") },
                    isError = title.length in 1..3
                )

                if (title.length in 1..3) {
                    Text("El título debe tener entre 4 y 15 caracteres", color = Color.Red, fontSize = 12.sp)
                }

                Spacer(modifier = Modifier.height(12.dp))
                Text("Categoría", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                CategoryDropdownMenu(selectedCategory, categories) { selectedCategory = it }

                Spacer(modifier = Modifier.height(12.dp))
                Text("Descripción", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                OutlinedTextField(
                    value = description,
                    onValueChange = { if (it.length in 0..100) description = it },
                    modifier = Modifier.fillMaxWidth().height(100.dp),
                    textStyle = TextStyle(fontSize = 16.sp),
                    placeholder = { Text("Introduce entre 20 y 100 caracteres") },
                    isError = description.length in 1..19
                )

                if (description.length in 1..19) {
                    Text("La descripción debe tener al menos 20 caracteres", color = Color.Red, fontSize = 12.sp)
                }

                Spacer(modifier = Modifier.height(12.dp))
                Text("Precio", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                OutlinedTextField(
                    value = price,
                    onValueChange = { if (it.all { char -> char.isDigit() || char == '.' }) price = it },
                    modifier = Modifier.fillMaxWidth(),
                    textStyle = TextStyle(fontSize = 16.sp),
                    placeholder = { Text("Introduce el precio") },
                    isError = price.isNotEmpty() && !isValidPrice(price)
                )

                if (price.isNotEmpty() && !isValidPrice(price)) {
                    Text("Introduce un precio válido", color = Color.Red, fontSize = 12.sp)
                }

                Spacer(modifier = Modifier.height(12.dp))
                Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                    Image(
                        painter = rememberAsyncImagePainter(imageUri ?: "data:image/png;base64,$encodedImage"),
                        contentDescription = "Imagen seleccionada",
                        modifier = Modifier
                            .size(150.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(Color.LightGray)
                            .clickable { imagePickerLauncher.launch("image/*") }
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))
                Button(
                    onClick = {
                        when {
                            title.length < 4 || title.length > 15 -> Toast.makeText(context, "El título debe tener entre 4 y 15 caracteres", Toast.LENGTH_SHORT).show()
                            description.length < 20 -> Toast.makeText(context, "La descripción debe tener al menos 20 caracteres", Toast.LENGTH_SHORT).show()
                            price.isEmpty() || !isValidPrice(price) -> Toast.makeText(context, "Introduce un precio válido", Toast.LENGTH_SHORT).show()
                            selectedCategory == null -> Toast.makeText(context, "Selecciona una categoría", Toast.LENGTH_SHORT).show()
                            encodedImage.isEmpty() -> Toast.makeText(context, "Debes seleccionar una imagen", Toast.LENGTH_SHORT).show()
                            else -> {
                                val updatedAd = Ad(
                                    title,
                                    description,
                                    encodedImage,
                                    price.toDouble(),
                                    ad.creationDate,
                                    user,
                                    selectedCategory!!
                                ).apply { setId(ad.id) }

                                coroutineScope.launch {
                                    adsViewModel.updateAd(updatedAd,
                                        onSuccess = {
                                            Toast.makeText(context, "Anuncio actualizado", Toast.LENGTH_SHORT).show()
                                            navController.popBackStack()
                                        },
                                        onError = { errorMessage ->
                                            Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show()
                                        }
                                    )
                                }
                            }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFAA00)),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text("Actualizar anuncio", color = Color.White, fontSize = 18.sp)
                }
            }
        }
    }
}


fun encodeImage(inputStream: InputStream?): String {
    return inputStream?.use {
        val bytes = it.readBytes()
        Base64.encodeToString(bytes, Base64.DEFAULT)
    } ?: ""
}

@Composable
fun CategoryDropdownMenu(
    selectedCategory: Category?,
    categories: List<Category>,
    onCategorySelected: (Category) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxWidth()) {
        OutlinedButton(onClick = { expanded = true }, modifier = Modifier.fillMaxWidth()) {
            Text(selectedCategory?.name ?: "Selecciona una categoría")
        }
        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            categories.forEach { category ->
                DropdownMenuItem(
                    text = { Text(category.name) },
                    onClick = {
                        onCategorySelected(category)
                        expanded = false
                    }
                )
            }
        }
    }
}
