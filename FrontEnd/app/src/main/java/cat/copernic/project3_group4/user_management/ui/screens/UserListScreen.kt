package cat.copernic.project3_group4.user_management.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import cat.copernic.project3_group4.core.models.User
import cat.copernic.project3_group4.core.ui.theme.BrownTertiary
import cat.copernic.project3_group4.core.ui.theme.OrangePrimary
import cat.copernic.project3_group4.user_management.data.datasource.UserApiRest
import cat.copernic.project3_group4.user_management.data.datasource.UserRetrofitInstance
import kotlinx.coroutines.launch

// Definir el color marrón terciario

@Composable
fun UserListScreen(navController: NavController, modifier: Modifier = Modifier) {
    val retrofit = UserRetrofitInstance.retrofitInstance
    val userApi = retrofit.create(UserApiRest::class.java)

    val users = remember { mutableStateListOf<User>() }
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        coroutineScope.launch {
            try {
                val response = userApi.getAllUsers()
                if (response.isSuccessful) {
                    users.clear()
                    users.addAll(response.body() ?: emptyList())
                } else {
                    println("Error: Response not successful")
                }
            } catch (e: Exception) {
                println("Error: ${e.message}")
            }
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color.White) // Fondo general blanco
    ) {
        // CABECERA NARANJA
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(OrangePrimary)
                .padding(vertical = 16.dp, horizontal = 16.dp),
            contentAlignment = Alignment.CenterStart
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(
                    onClick = { navController.navigate("profile") }
                ) {
                    Icon(
                        imageVector = Icons.Default.KeyboardArrowLeft,
                        contentDescription = "Volver",
                        tint = Color.White
                    )
                }
                Text(
                    text = "Usuarios (${users.size})",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
        }

        LazyColumn(modifier = Modifier.fillMaxSize()) {
            items(users) { user ->
                UserItem(user, navController, userApi, users)
            }
        }
    }
}

@Composable
fun UserItem(user: User, navController: NavController, userApi: UserApiRest, users: MutableList<User>) {
    val coroutineScope = rememberCoroutineScope()
    var showDialog by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        colors = CardDefaults.cardColors(containerColor = OrangePrimary) // Fondo marrón
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = user.name, fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.White)
            Text(text = user.email, fontSize = 14.sp, color = Color.White)
            Text(text = "Teléfono: ${user.phoneNumber}", fontSize = 14.sp, color = Color.White)
            Text(text = "Estado: ${if (user.isStatus) "Activo" else "Inactivo"}", fontSize = 14.sp, color = Color.White)
            Text(text = "Rol: ${user.role}", fontSize = 14.sp, color = Color.White)

            Spacer(modifier = Modifier.height(8.dp))

            Row {
                Button(
                    onClick = { navController.navigate("edit_user/${user.id}") },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(containerColor = BrownTertiary) // Botón naranja
                ) {
                    Text("Editar", color = Color.White)
                }

                Spacer(modifier = Modifier.width(8.dp))

                Button(
                    onClick = { showDialog = true },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("Eliminar", color = Color.White)
                }

                Spacer(modifier = Modifier.width(8.dp))

                Button(
                    onClick = {
                        coroutineScope.launch {
                            try {
                                val response = if (user.isStatus) userApi.desactivateUserStatus(user.id) else userApi.activateUserStatus(user.id)
                                if (response.isSuccessful) {
                                    println("Usuario activado")

                                    // Buscar y actualizar el usuario en la lista
                                    val index = users.indexOfFirst { it.id == user.id }
                                    if (index != -1) {
                                        users[index] = User(
                                            users[index].id,
                                            users[index].name,
                                            users[index].email,
                                            users[index].phoneNumber,
                                            !users[index].isStatus,  // Invierte el estado
                                            users[index].role
                                        )

                                    }
                                } else {
                                    println("Error al actualizar estado del usuario")
                                }
                            } catch (e: Exception) {
                                println("Error: ${e.message}")
                            }
                        }
                    },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.White)
                ) {
                    Text(if (user.isStatus) "Desactivar" else "Activar", color = OrangePrimary)
                }


            }
        }
    }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text("Confirmar eliminación", fontWeight = FontWeight.Bold, color = BrownTertiary) },
            text = { Text("¿Seguro que quieres eliminar a ${user.name}? Esta acción no se puede deshacer.", color = Color.Black) },
            confirmButton = {
                Button(
                    onClick = {
                        coroutineScope.launch {
                            try {
                                val response = userApi.deleteUser(user.id)
                                if (response.isSuccessful) {
                                    users.remove(user)
                                } else {
                                    println("Error al eliminar usuario")
                                }
                            } catch (e: Exception) {
                                println("Error: ${e.message}")
                            }
                        }
                        showDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("Eliminar", color = Color.White)
                }
            },
            dismissButton = {
                Button(
                    onClick = { showDialog = false },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.LightGray)
                ) {
                    Text("Cancelar", color = Color.Black)
                }
            }
        )
    }
}
