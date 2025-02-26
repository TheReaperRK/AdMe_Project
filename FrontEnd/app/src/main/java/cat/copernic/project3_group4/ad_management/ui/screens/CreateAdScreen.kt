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
import cat.copernic.project3_group4.category_management.ui.viewmodels.CategoryViewModel
import cat.copernic.project3_group4.core.models.Ad
import cat.copernic.project3_group4.core.models.Category
import cat.copernic.project3_group4.core.models.User
import cat.copernic.project3_group4.ad_management.data.datasource.AdApiRest
import cat.copernic.project3_group4.ad_management.data.datasource.AdRetrofitInstance
import cat.copernic.project3_group4.main.screens.BottomNavigationBar
import coil.compose.rememberAsyncImagePainter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.InputStream
import java.text.SimpleDateFormat
import java.util.*

fun isValidPrice(price: String): Boolean {
    return price.toDoubleOrNull() != null && price.toDouble() >= 0
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateAdScreen(navController: NavController, viewModel: CategoryViewModel, userState: MutableState<User?>) {
    val user = userState.value
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var price by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf<Category?>(null) }
    var imageUri by remember { mutableStateOf<Uri?>(null) }
    var encodedImage by remember { mutableStateOf("") }
    val context = LocalContext.current
    val categories by viewModel.categories.collectAsState(initial = emptyList())

    val imagePickerLauncher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let { selectedUri ->
            imageUri = selectedUri
            encodedImage = encodedImage(context.contentResolver.openInputStream(selectedUri))
        }
    }

    Column(modifier = Modifier.fillMaxSize().background(Color.White)) {
        SmallTopAppBar(
            title = { Text("Crear Anuncio", color = Color.White) },
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
            CategoriDropdownMenu(selectedCategory, categories) { selectedCategory = it }

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
                placeholder = { Text("Introduce el precio") }
            )

            Spacer(modifier = Modifier.height(12.dp))
            Text("Imagen", fontSize = 18.sp, fontWeight = FontWeight.Bold)
            Box(
                modifier = Modifier
                    .size(150.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(Color.LightGray)
                    .clickable { imagePickerLauncher.launch("image/*") }
            ) {
                imageUri?.let {
                    Image(
                        painter = rememberAsyncImagePainter(it),
                        contentDescription = "Imagen seleccionada",
                        modifier = Modifier.fillMaxSize()
                    )
                } ?: run {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Añadir imagen",
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))
            Button(
                onClick = {
                    when {
                        title.length < 4 -> Toast.makeText(context, "El título debe tener entre 4 y 15 caracteres", Toast.LENGTH_SHORT).show()
                        description.length < 20 -> Toast.makeText(context, "La descripción debe tener al menos 20 caracteres", Toast.LENGTH_SHORT).show()
                        price.isEmpty() || !isValidPrice(price) -> Toast.makeText(context, "Introduce un precio válido", Toast.LENGTH_SHORT).show()
                        selectedCategory == null -> Toast.makeText(context, "Selecciona una categoría", Toast.LENGTH_SHORT).show()
                        encodedImage.isEmpty() -> Toast.makeText(context, "Debes seleccionar una imagen", Toast.LENGTH_SHORT).show()
                        else -> {
                            val newAd = Ad(
                                title,
                                description,
                                encodedImage,
                                price.toDouble(),
                                SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date()),
                                user,
                                selectedCategory!!
                            )
                            CoroutineScope(Dispatchers.IO).launch {
                                val response = AdRetrofitInstance.create(AdApiRest::class.java).createAd(newAd)
                                (context as Activity).runOnUiThread {
                                    if (response.isSuccessful) {
                                        Toast.makeText(context, "Anuncio creado correctamente", Toast.LENGTH_SHORT).show()
                                        navController.navigate("categoryScreen")
                                    } else {
                                        Toast.makeText(context, "Error al crear el anuncio", Toast.LENGTH_LONG).show()
                                    }
                                }
                            }
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFAA00)),
                shape = RoundedCornerShape(10.dp)
            ) {
                Text("Crear anuncio", color = Color.White, fontSize = 18.sp)
            }
        }

        Spacer(modifier = Modifier.weight(1f))
        BottomNavigationBar(navController)
    }
}



@Composable
fun CategoriDropdownMenu(
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

fun encodedImage(inputStream: InputStream?): String {
    return inputStream?.use {
        val bytes = it.readBytes()
        Base64.encodeToString(bytes, Base64.DEFAULT)
    } ?: ""
}