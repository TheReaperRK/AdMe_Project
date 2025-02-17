package cat.copernic.project3_group4.user_management.ui.screens

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.White
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import cat.copernic.project3_group4.R
import cat.copernic.project3_group4.core.ui.theme.OrangePrimary
import cat.copernic.project3_group4.core.utils.createPartFromString
import cat.copernic.project3_group4.core.utils.uriToMultipartBodyPart
import cat.copernic.project3_group4.user_management.data.datasource.AuthRetrofitInstance
import kotlinx.coroutines.launch
import coil.compose.rememberAsyncImagePainter


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterScreen(navController: NavController) {
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) } // Guarda la imagen seleccionada

    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        selectedImageUri = uri // Guardar la URI de la imagen seleccionada
    }

    Box(
        modifier = Modifier.fillMaxSize().background(OrangePrimary),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.padding(32.dp)) {
            IconButton(
                onClick = { navController.popBackStack() },
                modifier = Modifier
                    .padding(start = 16.dp, top = 16.dp) // Espaciado desde los bordes
                    .size(48.dp)
                    .align(Alignment.Start)
                    .offset(y = -20.dp) // Mueve la flecha más arriba
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Volver",
                    tint = Color.White
                )
            }
            // Mostrar la imagen seleccionada o un ícono por defecto
            selectedImageUri?.let {
                Image(
                    painter = rememberAsyncImagePainter(it),
                    contentDescription = "Imagen de perfil",
                    modifier = Modifier
                        .size(100.dp)
                        .clip(RoundedCornerShape(50.dp))
                        .background(White)
                        .clickable { launcher.launch("image/*") }
                )
            } ?: Image(
                painter = painterResource(id = R.drawable.ic_logo),
                contentDescription = "Seleccionar imagen",
                modifier = Modifier
                    .size(100.dp)
                    .clickable { launcher.launch("image/*") }
            )

            Spacer(modifier = Modifier.height(16.dp))
            TextField(value = name, onValueChange = { name = it }, label = { Text("Nombre") })
            TextField(value = email, onValueChange = { email = it }, label = { Text("Correo") })
            TextField(value = phone, onValueChange = { phone = it }, label = { Text("Teléfono") })
            TextField(value = password, onValueChange = { password = it }, label = { Text("Contraseña") }, visualTransformation = PasswordVisualTransformation())
            TextField(value = confirmPassword, onValueChange = { confirmPassword = it }, label = { Text("Confirmar Contraseña") }, visualTransformation = PasswordVisualTransformation())

            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = {
                    if (password == confirmPassword) {
                        coroutineScope.launch {
                            val emailPart = createPartFromString(email)
                            val namePart = createPartFromString(name)
                            val phonePart = createPartFromString(phone)
                            val passwordPart = createPartFromString(password)

                            // Convertir la imagen en MultipartBody.Part
                            val imagePart = selectedImageUri?.let { uriToMultipartBodyPart(context, it) }

                            val response = AuthRetrofitInstance.authApi.register(
                                namePart, emailPart, phonePart, passwordPart, imagePart
                            )

                            if (response.isSuccessful) {
                                Toast.makeText(context, "Registro exitoso", Toast.LENGTH_SHORT).show()
                                navController.navigate("login")
                            } else {
                                Toast.makeText(context, "Error en el registro", Toast.LENGTH_SHORT).show()
                            }
                        }
                    } else {
                        Toast.makeText(context, "Las contraseñas no coinciden", Toast.LENGTH_SHORT).show()
                    }
                }
            )
            {
                Text("Registrarse")
            }
        }
    }
}


