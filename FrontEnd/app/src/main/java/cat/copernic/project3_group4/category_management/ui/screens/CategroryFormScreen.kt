package cat.copernic.project3_group4.category_management.ui.screens

import android.content.ContentResolver
import android.graphics.BitmapFactory
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import cat.copernic.project3_group4.R
import cat.copernic.project3_group4.category_management.data.datasource.CategoryApiRest
import cat.copernic.project3_group4.category_management.data.datasource.CategoryRetrofitInstance
import cat.copernic.project3_group4.category_management.ui.viewmodels.CategoryViewModel
import cat.copernic.project3_group4.core.models.Category
import cat.copernic.project3_group4.core.models.User
import cat.copernic.project3_group4.core.utils.createPartFromString
import cat.copernic.project3_group4.core.utils.uriToMultipartBodyPart
import cat.copernic.project3_group4.user_management.data.datasource.AuthRetrofitInstance
import coil.compose.rememberAsyncImagePainter
import kotlinx.coroutines.launch
import okhttp3.ResponseBody
import retrofit2.create
import java.io.ByteArrayOutputStream
import java.io.InputStream

@Composable
fun CategoryFormScreen(categoryViewModel: CategoryViewModel, userState: MutableState<User?>, navController: NavController ){
    val user = userState.value

    if(user == null){
        Text("No hay usuario autenticado", textAlign = TextAlign.Center, modifier = Modifier.fillMaxSize())
        return
    }

   // val categories = categoryViewModel.categories.observeAsState(initial = emptyList())
    var name  by remember { mutableStateOf("") }
    var description  by remember { mutableStateOf("") }
    var proposal by remember { mutableStateOf<Boolean>(false) }

    var selectedImageUri by remember { mutableStateOf<Uri?>(null) } // Guarda la imagen seleccionada
    var imageSelected = false

    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        selectedImageUri = uri // Guardar la URI de la imagen seleccionada
    }


    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White) // Fondo blanco
            .padding(12.dp)
            .verticalScroll(rememberScrollState()), // Agregamos verticalScroll
        horizontalAlignment = Alignment.CenterHorizontally

    ) {
        Text("Proponer Categoria", color = Color.Black, fontSize = 38.sp, fontWeight = FontWeight.W500, modifier = Modifier.padding(16.dp))
        Spacer(modifier = Modifier.height(20.dp))
        Box(
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            // Mostrar la imagen seleccionada o un ícono por defecto
            selectedImageUri?.let {
                Image(
                    painter = rememberAsyncImagePainter(it),
                    contentDescription = "Imagtge de categoria",
                    modifier = Modifier
                        .height(300.dp)
                        .width(450.dp)
                        .clip(RoundedCornerShape(50.dp))
                        .background(White)
                        .clickable { launcher.launch("image/*") }
                )
                imageSelected = true
            } ?: Image(
                painter = painterResource(id = R.drawable.ic_logo),
                contentDescription = "Seleccionar imagen",
                modifier = Modifier
                    .height(300.dp)
                    .width(450.dp)
                    .clickable { launcher.launch("image/*") }
            )
            imageSelected = false
        }
        Box(
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.White) // Fondo blanco
                    .padding(12.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(40.dp))
                Text(
                    "Nombre de la categoria",
                    color = Color.Black,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.W500,
                    modifier = Modifier.padding(16.dp)
                )

                TextField(value = name, onValueChange = { name = it }, label = { Text("Nombre") })
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    "Descripcion de la categoria",
                    color = Color.Black,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.W500,
                    modifier = Modifier.padding(16.dp).align(Alignment.CenterHorizontally)
                )

                TextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Descripcion") },
                    modifier = Modifier.height(200.dp)
                )
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
        if(user.role.name == "USER"){
            Text("Tu propuesta sera avaluada por nuestro equipo para su posterior implementación.", color = Color.Black, fontSize = 17.sp)
        }

        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = {
                if(user.role.name == "USER"){
                    proposal = true
                }else{
                    proposal = false
                }
                if (name.isNotBlank() and description.isNotBlank()) {
                    coroutineScope.launch {

                        /*val namePart = createPartFromString(name)
                        val descriptionPart = createPartFromString(description)
                        val proposalPart = createPartFromString(if (proposal) "true" else "false")
                        */
                        // Convertir la imagen en MultipartBody.Part
                        //val imagePart = selectedImageUri?.let { uriToMultipartBodyPart(context, it) }
                        val byteArray = selectedImageUri?.let { convertUriToByteArray(it, context.contentResolver) }
                        val response = CategoryRetrofitInstance.retrofitInstance.create<CategoryApiRest>().createCategory(
                            Category(null,name,description,byteArray.toString(),proposal)
                        )

                        if (response.isSuccessful) {
                            Toast.makeText(context, "creacion exitosa", Toast.LENGTH_SHORT).show()
                            navController.navigate("categoryScreen")
                        } else {
                            Toast.makeText(context, "Error en la creacion", Toast.LENGTH_SHORT).show()
                        }
                    }
                } else {
                    Toast.makeText(context, "Los campos estan incompletos", Toast.LENGTH_SHORT).show()
                }
            },
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = Black)

            //colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFA500))

        ){
            if(user.role.name == "ADMIN"){
                Text("Crear categoria", fontSize = 25.sp, fontWeight = FontWeight.W800, color = White)
            }else{
                Text("Proponer categoria", fontSize = 25.sp, fontWeight = FontWeight.W800, color = White)
            }
        }


    }
}
fun convertUriToByteArray(uri: Uri, contentResolver: ContentResolver): ByteArray? {
    var byteArray: ByteArray? = null
    try {
        // Abrir el flujo de entrada para leer el archivo de la imagen
        val inputStream: InputStream? = contentResolver.openInputStream(uri)

        // Verificar que el flujo no sea nulo
        inputStream?.let {
            // Crear un ByteArrayOutputStream para escribir los bytes
            val byteArrayOutputStream = ByteArrayOutputStream()
            val buffer = ByteArray(1024)
            var length: Int

            // Leer los bytes en bloques del flujo de entrada
            while (it.read(buffer).also { length = it } != -1) {
                byteArrayOutputStream.write(buffer, 0, length)
            }

            // Convertir el contenido del ByteArrayOutputStream en un ByteArray
            byteArray = byteArrayOutputStream.toByteArray()
            byteArrayOutputStream.close()
        }

        inputStream?.close()
    } catch (e: Exception) {
        e.printStackTrace()
    }

    return byteArray
}