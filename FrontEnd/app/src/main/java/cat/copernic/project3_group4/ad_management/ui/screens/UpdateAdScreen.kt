package cat.copernic.project3_group4.ad_management.ui.screens

import android.app.Activity
import android.graphics.BitmapFactory
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
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import cat.copernic.project3_group4.R
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
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
    val categories by viewModel.categories.observeAsState(initial = emptyList())
    val coroutineScope = rememberCoroutineScope()

    val titleError = title.length !in 4..20
    val descriptionError = description.length !in 10..100
    val priceError = price.isEmpty() || !isValidPrice(price)
    val categoryError = selectedCategory == null
    val imageError = encodedImage.isEmpty()

    val formValid = !titleError && !descriptionError && !priceError && !categoryError && !imageError

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
                title = { Text(stringResource(R.string.update_ad), color = Color.White) },
                colors = TopAppBarDefaults.smallTopAppBarColors(containerColor = Color(0xFFFF6600))
            )

            Column(modifier = Modifier.padding(16.dp)) {
                Text(stringResource(R.string.title), fontSize = 18.sp, fontWeight = FontWeight.Bold)
                OutlinedTextField(
                    value = title,
                    onValueChange = { if (it.length <= 20) title = it },
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
                CategoryDropdownMenu(selectedCategory, categories) { selectedCategory = it }
                if (categoryError) {
                    Text(stringResource(R.string.category_error), color = Color.Red, fontSize = 12.sp)
                }

                Spacer(modifier = Modifier.height(12.dp))
                Text(stringResource(R.string.description), fontSize = 18.sp, fontWeight = FontWeight.Bold)
                OutlinedTextField(
                    value = description,
                    onValueChange = { if (it.length <= 100) description = it },
                    modifier = Modifier.fillMaxWidth(),
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
                Box(
                    modifier = Modifier
                        .size(150.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(Color.LightGray)
                        .clickable { imagePickerLauncher.launch("image/*") },
                    contentAlignment = Alignment.Center
                ) {
                    val imageBitmap by remember(encodedImage) {
                        mutableStateOf(base64ToImageBitmap(encodedImage))
                    }

                    imageBitmap?.let {
                        Image(
                            bitmap = it,
                            contentDescription = stringResource(R.string.selected_image),
                            modifier = Modifier.size(150.dp)
                        )
                    } ?: Text(stringResource(R.string.click_to_select_image), color = Color.Gray)
                }

                Spacer(modifier = Modifier.height(12.dp))
                Button(
                    onClick = {
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
                                    Toast.makeText(context, context.getString(R.string.ad_updated_success), Toast.LENGTH_SHORT).show()
                                    navController.popBackStack()
                                },
                                onError = { errorMessage ->
                                    Toast.makeText(context, context.getString(R.string.ad_update_error), Toast.LENGTH_SHORT).show()
                                }
                            )
                        }
                    },
                    modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFAA00)),
                    shape = RoundedCornerShape(10.dp),
                    enabled = formValid
                ) {
                    Text(stringResource(R.string.update_ad), color = Color.White, fontSize = 18.sp)
                }
            }
        }
    }
}


fun encodeImage(inputStream: InputStream?): String {
    return inputStream?.use {
        val bytes = it.readBytes()
        Base64.encodeToString(bytes, Base64.NO_WRAP)
    } ?: ""
}

fun base64ToImageBitmap(base64: String): ImageBitmap? {
    return try {
        val decodedBytes = Base64.decode(base64, Base64.DEFAULT)
        val bitmap = BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.size)
        bitmap?.asImageBitmap()
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}


@Composable
fun CategoryDropdownMenu(
    selectedCategory: Category?,
    categories: List<Category>,
    onCategorySelected: (Category) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    val selectCategoryText = stringResource(id = R.string.category_error)

    Box(modifier = Modifier.fillMaxWidth()) {
        OutlinedButton(onClick = { expanded = true }, modifier = Modifier.fillMaxWidth()) {
            Text(selectedCategory?.name ?: selectCategoryText)
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
