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


        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
        ) {
            SmallTopAppBar(
                title = { Text("Crear Anuncio", color = Color.White) },
                colors = TopAppBarDefaults.smallTopAppBarColors(containerColor = Color(0xFFFF6600))
            )

            Column(modifier = Modifier.padding(16.dp)) {
                Text("Título", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    modifier = Modifier.fillMaxWidth(),
                    textStyle = TextStyle(fontSize = 16.sp),
                    placeholder = { Text("Introduce el título") }
                )

                Spacer(modifier = Modifier.height(12.dp))
                Text("Categoría", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                CategoriDropdownMenu(selectedCategory, categories) { selectedCategory = it }

                Spacer(modifier = Modifier.height(12.dp))
                Text("Descripción", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(100.dp),
                    textStyle = TextStyle(fontSize = 16.sp),
                    placeholder = { Text("Introduce la descripción") }
                )

                Spacer(modifier = Modifier.height(12.dp))
                Text("Precio", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                OutlinedTextField(
                    value = price,
                    onValueChange = { price = it },
                    modifier = Modifier.fillMaxWidth(),
                    textStyle = TextStyle(fontSize = 16.sp),
                    placeholder = { Text("Introduce el precio") }
                )

                Spacer(modifier = Modifier.height(12.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    if (imageUri != null) {
                        Image(
                            painter = rememberAsyncImagePainter(imageUri),
                            contentDescription = "Imagen seleccionada",
                            modifier = Modifier
                                .size(150.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(Color.LightGray)
                        )
                    } else {
                        Image(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Añadir imagen",
                            modifier = Modifier
                                .size(48.dp)
                                .clickable { imagePickerLauncher.launch("image/*") }
                        )
                    }
                }


                Spacer(modifier = Modifier.height(12.dp))
                Button(
                    onClick = {
                        if (title.isNotEmpty() && description.isNotEmpty() && price.isNotEmpty() && selectedCategory != null) {
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
                                val retrofit = Retrofit.Builder()
                                    .baseUrl("https://tu-api.com/")
                                    .addConverterFactory(GsonConverterFactory.create())
                                    .build()

                                val adApi = AdRetrofitInstance.create(AdApiRest::class.java)

                                val response = adApi.createAd(newAd)

                                if (response.isSuccessful) {
                                    (context as Activity).runOnUiThread {
                                        Toast.makeText(context, "Anuncio creado correctamente", Toast.LENGTH_SHORT).show()
                                        navController.navigate("categoryScreen")
                                    }
                                } else {
                                    val errorBody = response.errorBody()?.string()
                                    (context as Activity).runOnUiThread {
                                        Toast.makeText(
                                            context,
                                            "Error al crear el anuncio: ${response.code()} - $errorBody",
                                            Toast.LENGTH_LONG
                                        ).show()
                                    }
                                }
                            }
                        } else {
                            (context as Activity).runOnUiThread {
                                Toast.makeText(context, "Completa todos los campos", Toast.LENGTH_SHORT).show()
                            }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFAA00)),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text("Crear anuncio", color = Color.White, fontSize = 18.sp)
                }
            }

            Spacer(modifier = Modifier.weight(1f)) // Empuja el contenido hacia arriba para que el BottomNavigationBar esté siempre abajo
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