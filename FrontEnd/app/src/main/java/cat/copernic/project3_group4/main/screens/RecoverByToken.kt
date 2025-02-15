package cat.copernic.project3_group4.main.screens


import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color.Companion.Black
import androidx.compose.ui.graphics.Color.Companion.White
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import cat.copernic.project3_group4.R
import cat.copernic.project3_group4.core.ui.theme.OrangePrimary
import cat.copernic.project3_group4.user_management.data.datasource.AuthRetrofitInstance
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject

@Composable
fun RecoverByToken(navController: NavController) {
    var email by remember { mutableStateOf("")}
    var token by remember { mutableStateOf("")}
    var word by remember { mutableStateOf("")}
    var wordRepeat by remember { mutableStateOf("")}


    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(OrangePrimary),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(32.dp)
        ) {
            // Botón de regreso
            IconButton(
                onClick = { navController.popBackStack() },
                modifier = Modifier
                    .padding(start = 16.dp, top = 16.dp)
                    .align(Alignment.Start)
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Volver",
                    tint = White
                )
            }

            Image(
                painter = painterResource(id = R.drawable.ic_logo),
                contentDescription = "Logo",
                modifier = Modifier.size(100.dp)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Recuperar Contraseña",
                style = TextStyle(fontSize = 24.sp, fontWeight = FontWeight.Bold, color = White)
            )
            Spacer(modifier = Modifier.height(16.dp))

            // Campo para el correo
            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Introduce tu correo:") },
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Campo para el correo
            OutlinedTextField(
                value = token,
                onValueChange = { token = it },
                label = { Text("Introduce el token:") },
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Campo para el correo
            OutlinedTextField(
                value = word,
                onValueChange = { word = it },
                label = { Text("Introduce la nueva contraseña:") },
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Campo para el correo
            OutlinedTextField(
                value = wordRepeat,
                onValueChange = { wordRepeat = it },
                label = { Text("repite la nueva contraseña:") },
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Botón para enviar la solicitud de recuperación
            Button(
                onClick = {
                    coroutineScope.launch {
                        if (word == wordRepeat) {

                            val emailRequestBody: RequestBody =
                                email.toRequestBody("text/plain".toMediaTypeOrNull())
                            val tokenRequestBody: RequestBody =
                                token.toRequestBody("text/plain".toMediaTypeOrNull())
                            val wordRequestBody: RequestBody =
                                word.toRequestBody("text/plain".toMediaTypeOrNull())


                            val response = AuthRetrofitInstance.authApi.resetPassword(
                                emailRequestBody,
                                tokenRequestBody,
                                wordRequestBody
                            )

                            if (response.isSuccessful) {
                                Toast.makeText(
                                    context,
                                    "Se ha restablecido la contraseña",
                                    Toast.LENGTH_LONG
                                ).show()
                                navController.navigate("login")
                            } else {
                                val errorBody = response.errorBody()?.string() // Obtener el cuerpo de la respuesta de error
                                val errorMessage = try {
                                    JSONObject(errorBody).getString("message") // Extraer el mensaje del JSON
                                } catch (e: Exception) {
                                    "Error con los datos introducidos" // Mensaje por defecto en caso de fallo
                                }

                                Toast.makeText(
                                    context,
                                    errorMessage,
                                    Toast.LENGTH_SHORT
                                ).show()
                            }

                        } else {
                            Toast.makeText(
                                context,
                                "Las contraseñas no coinciden",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                },
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = Black)
            ) {
                Text("Restablecer Contraseña", color = White)
            }
        }
    }
}
