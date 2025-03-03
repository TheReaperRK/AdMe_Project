package cat.copernic.project3_group4.main.screens


import RegisterViewModel
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import cat.copernic.project3_group4.R
import cat.copernic.project3_group4.core.ui.theme.BrownTertiary
import cat.copernic.project3_group4.core.ui.theme.OrangePrimary
import cat.copernic.project3_group4.user_management.data.datasource.AuthRetrofitInstance
import cat.copernic.project3_group4.user_management.ui.screens.InputField
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject

@Composable
fun RecoverByToken(navController: NavController) {
    var email by remember { mutableStateOf("") }
    var token by remember { mutableStateOf("") }
    var word by remember { mutableStateOf("") }
    var wordRepeat by remember { mutableStateOf("") }

    var emailError by remember { mutableStateOf<String?>(null) }
    var tokenError by remember { mutableStateOf<String?>(null) }
    var passwordError by remember { mutableStateOf<String?>(null) }
    var repeatPasswordError by remember { mutableStateOf<String?>(null) }

    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current

    Box(
        modifier = Modifier.fillMaxSize().background(OrangePrimary),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.padding(32.dp)) {
            // Botón de regreso
            IconButton(onClick = { navController.popBackStack() }) {
                Icon(imageVector = Icons.Default.ArrowBack, contentDescription = stringResource(R.string.back), tint = White)
            }

            Image(painter = painterResource(id = R.drawable.ic_logo), contentDescription = stringResource(R.string.logo), modifier = Modifier.size(100.dp))
            Spacer(modifier = Modifier.height(16.dp))
            Text(stringResource(R.string.recover_password), fontSize = 24.sp, fontWeight = FontWeight.Bold, color = White)
            Spacer(modifier = Modifier.height(16.dp))

            // Campo para el correo
            InputField(
                label = stringResource(R.string.email),
                value = email,
                onValueChange = { email = it },
                error = emailError,
                maxLength = 60
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Campo para el token
            InputField(
                label = stringResource(R.string.token),
                value = token,
                onValueChange = { token = it },
                error = tokenError,
                maxLength = 250
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Campo para la contraseña
            InputField(
                label = stringResource(R.string.new_password),
                value = word,
                onValueChange = { word = it },
                error = passwordError,
                maxLength = 60,
                isPassword = true
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Repetir contraseña
            InputField(
                label = stringResource(R.string.repeat_password),
                value = wordRepeat,
                onValueChange = { wordRepeat = it },
                error = repeatPasswordError,
                maxLength = 60,
                isPassword = true
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Botón de recuperación
            Button(
                onClick = {
                    emailError = if (!isValidEmail(email))context.getString(R.string.invalid_email) else null
                    tokenError = if (token.length > 250) context.getString(R.string.token_length_error) else null
                    passwordError = if (!isValidPassword(word)) context.getString(R.string.weak_password) else null
                    repeatPasswordError = if (word != wordRepeat) context.getString(R.string.passwords_do_not_match) else null

                    if (emailError == null && tokenError == null && passwordError == null && repeatPasswordError == null) {
                        coroutineScope.launch {
                            val response = AuthRetrofitInstance.authApi.resetPassword(
                                email.toRequestBody("text/plain".toMediaTypeOrNull()),
                                token.toRequestBody("text/plain".toMediaTypeOrNull()),
                                word.toRequestBody("text/plain".toMediaTypeOrNull())
                            )

                            if (response.isSuccessful) {
                                Toast.makeText(context, context.getString(R.string.password_reset_success), Toast.LENGTH_LONG).show()
                                navController.navigate("login")
                            } else {
                                val errorBody = response.errorBody()?.string()
                                val errorMessage = try {
                                    JSONObject(errorBody).getString("message")
                                } catch (e: Exception) {
                                    context.getString(R.string.generic_error)
                                }
                                emailError = if (errorMessage.contains("correo")) errorMessage else null
                                tokenError = if (errorMessage.contains("token")) errorMessage else null
                                passwordError = if (errorMessage.contains("contraseña")) errorMessage else null
                            }
                        }
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = BrownTertiary),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(stringResource(R.string.reset_password))
            }
        }
    }
}


fun isValidEmail(email: String) = email.length <= 60 && android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()

fun isValidPassword(password: String) =
    password.length >= 8 &&
            password.any { it.isUpperCase() } &&
            password.any { it.isLowerCase() } &&
            password.any { it.isDigit() } &&
            password.any { !it.isLetterOrDigit() }
