package cat.copernic.project3_group4.user_management.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import cat.copernic.project3_group4.core.models.User
import cat.copernic.project3_group4.core.ui.theme.OrangePrimary
import cat.copernic.project3_group4.core.utils.enums.Roles
import cat.copernic.project3_group4.user_management.data.datasource.UserApiRest
import cat.copernic.project3_group4.user_management.data.datasource.UserRetrofitInstance
import kotlinx.coroutines.launch

@Composable
fun EditUserScreen(userState: MutableState<User?>, navController: NavController) {

    val user = userState.value
    val retrofit = UserRetrofitInstance.retrofitInstance
    val userApi = retrofit.create(UserApiRest::class.java)
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var phoneNumber by remember { mutableStateOf("") }
    var word by remember { mutableStateOf("") }
    var isStatus by remember { mutableStateOf(true) }
    var selectedRole by remember { mutableStateOf(Roles.USER) } // Valor por defecto


    if (user == null) {
        Text("No hay usuario autenticado", textAlign = TextAlign.Center, modifier = Modifier.fillMaxSize())
        return
    }

    // Cargar el usuario desde el backend
    LaunchedEffect(user.id) {
        coroutineScope.launch {
            try {
                val response = userApi.getUserById(user.id)

                if (response.isSuccessful) {
                    response.body()?.let {
                        user
                        name = it.name
                        email = it.email
                        phoneNumber = it.phoneNumber
                        isStatus = it.isStatus
                        selectedRole = it.role
                    }
                } else {
                    Toast.makeText(context, "Error al cargar usuario", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(context, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Editar Usuario", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = OrangePrimary)
        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("Nombre") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Correo Electrónico") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = phoneNumber,
            onValueChange = { phoneNumber = it },
            label = { Text("Teléfono") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = word,
            onValueChange = { word = it },
            label = { Text("contraseña") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(12.dp))

        Row(verticalAlignment = Alignment.CenterVertically) {
            Text("Estado: ${if (isStatus) "Activo" else "Inactivo"}")
            Switch(checked = isStatus, onCheckedChange = { isStatus = it })
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Dropdown para seleccionar el rol
        var expanded by remember { mutableStateOf(false) }
        Box {
            Button(onClick = { expanded = true }) {
                Text("Rol: $selectedRole")
            }
            DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                Roles.values().forEach { role ->
                    DropdownMenuItem(
                        text = { Text(role.name) },
                        onClick = {
                            selectedRole = role
                            expanded = false
                        }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        Button(
            onClick = {
                coroutineScope.launch {
                    val updatedUser = User(name, email, phoneNumber, word, isStatus, selectedRole)
                    try {
                        val response = userApi.updateUser(user.id, updatedUser)
                        if (response.isSuccessful) {
                            Toast.makeText(context, "Usuario actualizado", Toast.LENGTH_SHORT).show()
                            navController.popBackStack()
                        } else {
                            Toast.makeText(context, "Error al actualizar", Toast.LENGTH_SHORT).show()
                        }
                    } catch (e: Exception) {
                        Toast.makeText(context, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                        println(e)
                    }
                }
            },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = OrangePrimary)
        ) {
            Text("Guardar Cambios", color = Color.White)
        }
    }
}
