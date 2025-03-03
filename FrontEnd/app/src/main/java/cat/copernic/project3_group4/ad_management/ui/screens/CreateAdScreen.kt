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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import cat.copernic.project3_group4.R
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
    LaunchedEffect(categories) {
        viewModel.fetchCategories()
    }

    val titleError = title.length in 1..3
    val descriptionError = description.length in 1..9
    val priceError = price.isEmpty() || !isValidPrice(price)
    val categoryError = selectedCategory == null
    val imageError = encodedImage.isEmpty()

    val isFormValid = !titleError && !descriptionError && !priceError && !categoryError && !imageError

    Column(modifier = Modifier.fillMaxSize().background(Color.White)) {
        SmallTopAppBar(
            title = { Text(stringResource(R.string.create_ad), color = Color.White) },
            colors = TopAppBarDefaults.smallTopAppBarColors(containerColor = Color(0xFFFF6600))
        )

        Column(modifier = Modifier.weight(1f).padding(16.dp)) {
            Text(stringResource(R.string.title), fontSize = 18.sp, fontWeight = FontWeight.Bold)
            OutlinedTextField(
                value = title,
                onValueChange = { if (it.length in 0..15) title = it },
                modifier = Modifier.fillMaxWidth(),
                textStyle = TextStyle(fontSize = 16.sp),
                placeholder = { Text(stringResource(R.string.title_placeholder)) },
                isError = titleError
            )
            if (titleError) {
                Text(stringResource(R.string.title_error), color = Color.Red, fontSize = 12.sp)
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(stringResource(R.string.category2), fontSize = 18.sp, fontWeight = FontWeight.Bold)
            CategoriDropdownMenu(selectedCategory, categories) { selectedCategory = it }
            if (categoryError) {
                Text(stringResource(R.string.category_error), color = Color.Red, fontSize = 12.sp)
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(stringResource(R.string.description), fontSize = 18.sp, fontWeight = FontWeight.Bold)
            OutlinedTextField(
                value = description,
                onValueChange = { if (it.length in 0..100) description = it },
                modifier = Modifier.fillMaxWidth().height(100.dp),
                textStyle = TextStyle(fontSize = 16.sp),
                placeholder = { Text(stringResource(R.string.description_placeholder)) },
                isError = descriptionError
            )
            if (descriptionError) {
                Text(stringResource(R.string.description_error), color = Color.Red, fontSize = 12.sp)
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(stringResource(R.string.price2), fontSize = 18.sp, fontWeight = FontWeight.Bold)
            OutlinedTextField(
                value = price,
                onValueChange = { if (it.all { char -> char.isDigit() || char == '.' }) price = it },
                modifier = Modifier.fillMaxWidth(),
                textStyle = TextStyle(fontSize = 16.sp),
                placeholder = { Text(stringResource(R.string.price_placeholder)) },
                isError = priceError
            )
            if (priceError) {
                Text(stringResource(R.string.price_error), color = Color.Red, fontSize = 12.sp)
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(stringResource(R.string.image), fontSize = 18.sp, fontWeight = FontWeight.Bold)
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
                        contentDescription = stringResource(R.string.selected_image),
                        modifier = Modifier.fillMaxSize()
                    )
                } ?: run {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = stringResource(R.string.add_image),
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
            }
            if (imageError) {
                Text(stringResource(R.string.image_error), color = Color.Red, fontSize = 12.sp)
            }

            Spacer(modifier = Modifier.height(12.dp))

            Button(
                onClick = {
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
                                Toast.makeText(context, context.getString(R.string.ad_created_success), Toast.LENGTH_SHORT).show()
                                navController.navigate("categoryScreen")
                            } else {
                                Toast.makeText(context, context.getString(R.string.ad_creation_error), Toast.LENGTH_LONG).show()
                            }
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFAA00)),
                shape = RoundedCornerShape(10.dp),
                enabled = isFormValid
            ) {
                Text(stringResource(R.string.create_ad), color = Color.White, fontSize = 18.sp)
            }
        }

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
    val selectCategoryText = stringResource(id = R.string.category_error) // "Selecciona una categoría"

    Box(modifier = Modifier.fillMaxWidth()) {
        OutlinedButton(onClick = { expanded = true }, modifier = Modifier.fillMaxWidth()) {
            Text(selectedCategory?.name ?: selectCategoryText)
        }
        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            categories.forEach { category ->
                if(!category.isProposal){
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
}


fun encodedImage(inputStream: InputStream?): String {
    return inputStream?.use {
        val bytes = it.readBytes()
        Base64.encodeToString(bytes, Base64.DEFAULT)
    } ?: ""
}