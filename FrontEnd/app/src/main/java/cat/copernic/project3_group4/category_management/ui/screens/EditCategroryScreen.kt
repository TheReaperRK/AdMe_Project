package cat.copernic.project3_group4.category_management.ui.screens

import android.content.ContentResolver
import android.content.ContentValues
import android.content.ContentValues.TAG
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Handler
import android.os.Looper
import android.util.Base64
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.ui.Modifier.*
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SmallTopAppBar
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.Black
import androidx.compose.ui.graphics.Color.Companion.White
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import cat.copernic.project3_group4.R
import cat.copernic.project3_group4.ad_management.ui.screens.base64ToByteArray
import cat.copernic.project3_group4.category_management.data.datasource.CategoryApiRest
import cat.copernic.project3_group4.category_management.data.datasource.CategoryRetrofitInstance
import cat.copernic.project3_group4.category_management.ui.viewmodels.CategoryViewModel
import cat.copernic.project3_group4.core.models.Ad
import cat.copernic.project3_group4.core.models.Category
import cat.copernic.project3_group4.core.models.User
import cat.copernic.project3_group4.core.ui.theme.BrownTertiary
import cat.copernic.project3_group4.core.ui.theme.OrangePrimary
import cat.copernic.project3_group4.core.utils.createPartFromString
import cat.copernic.project3_group4.core.utils.uriToMultipartBodyPart
import cat.copernic.project3_group4.main.screens.BottomNavigationBar
import cat.copernic.project3_group4.user_management.data.datasource.AuthRetrofitInstance
import coil.compose.rememberAsyncImagePainter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.create
import java.io.ByteArrayOutputStream
import java.io.InputStream
import java.sql.SQLException

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditCategoryScreen(
    categoryId: Long?,
    categoryViewModel: CategoryViewModel,
    userState: MutableState<User?>,
    navController: NavController
) {
    val user = userState.value
    if (user == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text(stringResource(R.string.no_user_authenticated))
        }
        return
    }
    val context = LocalContext.current

    var name by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var proposal by remember { mutableStateOf(false) }

    var imageBitmap by remember { mutableStateOf<ImageBitmap?>(null) }
    var byteArray by remember { mutableStateOf<ByteArray?>(null)}
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }
    val nameError = name.length in 1..3
    val descriptionError = description.length in 1..9

    val isFormValid = !nameError && !descriptionError
    LaunchedEffect(categoryId) {
        if(categoryId == null) {
            Handler(Looper.getMainLooper()).post {
                Toast.makeText(
                    context,
                    "ID de categoría inválido",
                    Toast.LENGTH_SHORT
                ).show()
                Log.e(
                    ContentValues.TAG,
                    "Error al cargar la categoria"
                )

            }
            navController.popBackStack()
        }else{
            categoryViewModel.fetchCategoryById(categoryId)
        }


    }
    val category by categoryViewModel.category.collectAsState()
    LaunchedEffect(category) {
        if (categoryId != null) {
            name = category.name ?: ""
            description = category.description ?: ""
            proposal = category.isProposal

            category.image?.let { base64String ->
                byteArray = base64ToByteArray(base64String)
                imageBitmap = byteArrayToImageBitmap(byteArray)
            }
        } else {
            Handler(Looper.getMainLooper()).post {
                Toast.makeText(
                    context,
                    "ID de categoría inválido",
                    Toast.LENGTH_SHORT
                ).show()
                Log.e(ContentValues.TAG, "Error al cargar la categoría")
            }
            navController.popBackStack()
        }
    }
    val coroutineScope = rememberCoroutineScope()
    var imageSelected by remember { mutableStateOf(false) }
    val imagePickerLauncher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        selectedImageUri = uri
        uri?.let {
            byteArray = convertUriToByteArray(it, context.contentResolver)
            byteArray?.let { bytes ->
                imageBitmap = byteArrayToImageBitmap(bytes)
            }
        }
    }

    Scaffold(
        bottomBar = { BottomNavigationBar(navController) }
    ) { paddingValues ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
                .paddingFromBaseline(0.dp, paddingValues.calculateBottomPadding()),
            horizontalAlignment = Alignment.Start,



        ) {
            TopAppBar(
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.Default.KeyboardArrowLeft,
                            contentDescription = stringResource(R.string.go_back),
                            tint = Color.White
                        )
                    }

                },
                title = {
                    Text(stringResource(R.string.modify_category), color = Color.White)
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFFFF6600))
            )

            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                   if(selectedImageUri != null){
                       Image(
                           painter = rememberAsyncImagePainter(selectedImageUri),
                           contentDescription = stringResource(R.string.category_image),
                           modifier = Modifier
                               .height(250.dp)
                               .width(350.dp)
                               .clip(RoundedCornerShape(50.dp))
                               .background(White)
                               .clickable { imagePickerLauncher.launch("image/*") }

                       )
                       imageSelected = true
                   }else if (byteArray != null && imageBitmap != null){
                       imageBitmap?.let {
                           Image(
                               bitmap = it,
                               contentDescription = stringResource(R.string.select_image),
                               modifier = Modifier
                                   .height(250.dp)
                                   .width(350.dp)
                                   .clickable { imagePickerLauncher.launch("image/*") }
                           )
                       }
                       imageSelected = true
                   }else{
                       Image(
                           painter = painterResource(id = R.drawable.add_image),
                           contentDescription = "Seleccionar imagen",
                           modifier = Modifier
                               .height(250.dp)
                               .width(350.dp)
                               .clickable { imagePickerLauncher.launch("image/*") }
                       )
                       imageSelected = false
                   }
                }
                Spacer(modifier = Modifier.height(12.dp))

                Text(stringResource(R.string.category_name), fontSize = 18.sp, fontWeight = FontWeight.Bold)
                OutlinedTextField(
                    value = name,
                    onValueChange = { if (it.length in 0..15) name = it },
                    modifier = Modifier.fillMaxWidth(),
                    textStyle = TextStyle(fontSize = 16.sp),
                    placeholder = { Text(stringResource(R.string.enter_name)) },
                    isError = nameError
                )
                if (nameError) {
                    Text(stringResource(R.string.name_error), color = Color.Red, fontSize = 12.sp)
                }
                Spacer(modifier = Modifier.height(12.dp))

                Text(stringResource(R.string.category_description), fontSize = 18.sp, fontWeight = FontWeight.Bold)
                OutlinedTextField(
                    value = description,
                    onValueChange = { if (it.length in 0..100) description = it },
                    modifier = Modifier.fillMaxWidth().height(120.dp),
                    textStyle = TextStyle(fontSize = 16.sp),
                    placeholder = { Text(stringResource(R.string.enter_description)) },
                    isError = descriptionError
                )
                if (descriptionError) {
                    Text(stringResource(R.string.description_error), color = Color.Red, fontSize = 12.sp)
                }

                Spacer(modifier = Modifier.height(12.dp))

                Button(
                    onClick = {

                        if (name.isNotBlank() && description.isNotBlank() && imageSelected && !nameError && !descriptionError) {

                                val namePart = createPartFromString(name)
                                val descriptionPart = createPartFromString(description)
                                val proposalPart = createPartFromString(if (proposal) "true" else "false")
                                val idPart = createPartFromString(categoryId.toString())
                                val imagePart = byteArray?.let {
                                    val requestBody = RequestBody.create("image/*".toMediaTypeOrNull(), it)
                                    MultipartBody.Part.createFormData("image", "category_image.jpg", requestBody)
                                }

                            coroutineScope.launch {
                                try{
                                    val response = categoryViewModel.updateCategory(
                                        idPart, namePart, descriptionPart, imagePart, proposalPart,
                                        onSuccess = {
                                            Handler(Looper.getMainLooper()).post {
                                                Toast.makeText(
                                                    context,
                                                    "Categoria actualizada",
                                                    Toast.LENGTH_SHORT
                                                ).show()
                                                navController.popBackStack()
                                            }
                                        },

                                        onError = { errorMessage ->
                                            Handler(Looper.getMainLooper()).post {
                                                Toast.makeText(
                                                    context,
                                                    errorMessage,
                                                    Toast.LENGTH_SHORT
                                                ).show()
                                            }
                                        }

                                    )

                                } catch (e: Exception) {
                                    Handler(Looper.getMainLooper()).post {
                                        Toast.makeText(
                                            context,
                                            "Error en la modificaciom",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                        Log.e(
                                            ContentValues.TAG,
                                            "Error al modificar la categoría: ${e.message}"
                                        )
                                    }
                                }
                            }
                        } else {
                            Toast.makeText(context, context.getString(R.string.complete_all_fields), Toast.LENGTH_SHORT).show()
                        }
                            Handler(Looper.getMainLooper()).post {
                                Toast.makeText(
                                    context,
                                    "Completa correctamente todos los campos",
                                    Toast.LENGTH_SHORT
                                ).show()
                                Log.e(
                                    ContentValues.TAG,
                                    "Error al categoria no modificada, campos incompletos o incorrectos"
                                )
                            }

                    },
                    modifier = Modifier.fillMaxWidth().padding(vertical = 20.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFAA00)),
                    shape = RoundedCornerShape(10.dp)
                ) {

                    Text("Modificar categoria", color = Color.White, fontSize = 18.sp)

                }

                Spacer(modifier = Modifier.height(12.dp))


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
fun byteArrayToImageBitmap(byteArray: ByteArray?): ImageBitmap {
    if(byteArray!=null) {
        try {

            val bitmap = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.size)
            return bitmap.asImageBitmap()
        } catch (e: IllegalArgumentException){
            throw  e
        }
    }else{
        throw IllegalArgumentException("El ByteArray es null")
    }
}
