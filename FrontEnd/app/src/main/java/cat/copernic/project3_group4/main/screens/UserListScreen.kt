package cat.copernic.project3_group4.main.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
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
import cat.copernic.project3_group4.user_management.data.datasource.UserApiRest
import cat.copernic.project3_group4.user_management.data.datasource.UserRetrofitInstance
import kotlinx.coroutines.launch

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

    Column(modifier = modifier.padding(16.dp)) {
        Button(
            onClick = { navController.navigate("profile") },
            modifier = Modifier
                .padding(16.dp)
                .size(48.dp)
                .align(Alignment.Start),
            shape = CircleShape,
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFA500))
        ) {
            Text("<", color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold)
        }
        Text(text = "Total de usuarios: ${users.size}", style = MaterialTheme.typography.titleLarge)

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
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = user.name, style = MaterialTheme.typography.titleMedium)
            Text(text = user.email, style = MaterialTheme.typography.bodyMedium)
            Text(text = "Teléfono: ${user.phoneNumber}", style = MaterialTheme.typography.bodyMedium)
            Text(text = "Estado: ${if (user.isStatus) "Activo" else "Inactivo"}", style = MaterialTheme.typography.bodyMedium)
            Text(text = "Rol: ${user.role}", style = MaterialTheme.typography.bodyMedium)

            Spacer(modifier = Modifier.height(8.dp))

            Row {
                Button(
                    onClick = { navController.navigate("edit_user/${user.id}") },
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Editar")
                }

                Spacer(modifier = Modifier.width(8.dp))

                Button(
                    onClick = { showDialog = true },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("Eliminar")
                }
            }
        }
    }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text("Confirmar eliminación") },
            text = { Text("¿Seguro que quieres eliminar a ${user.name}? Esta acción no se puede deshacer.") },
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
                    Text("Eliminar")
                }
            },
            dismissButton = {
                Button(onClick = { showDialog = false }) {
                    Text("Cancelar")
                }
            }
        )
    }
}
