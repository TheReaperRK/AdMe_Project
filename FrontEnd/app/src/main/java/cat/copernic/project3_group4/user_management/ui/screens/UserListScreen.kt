package cat.copernic.project3_group4.user_management.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import cat.copernic.project3_group4.R
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
            .background(Color.White)
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
                        contentDescription = stringResource(R.string.back),
                        tint = Color.White
                    )
                }
                Text(
                    text = stringResource(R.string.users_count, users.size),
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )

                IconButton(
                    onClick = { navController.navigate("create_user") }
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Añadir",
                        tint = Color.White
                    )
                }
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
        colors = CardDefaults.cardColors(containerColor = OrangePrimary)
    ) {
        Column(modifier = Modifier
            .padding(paddingValues = PaddingValues(bottom = 4.dp, top= 16.dp, end = 16.dp, start = 16.dp))
            .clip(RoundedCornerShape(topStartPercent = 0, topEndPercent = 0))
        ){


            Text(text = user.name, fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color.White)
            Text(text = user.email, fontSize = 14.sp, color = Color.White)
            Text(text = stringResource(R.string.phone_number, user.phoneNumber), fontSize = 14.sp, color = Color.White)
            Text(text = stringResource(R.string.status, if (user.isStatus) stringResource(R.string.active) else stringResource(R.string.inactive)), fontSize = 14.sp, color = Color.White)
            Text(text = stringResource(R.string.role, user.role), fontSize = 14.sp, color = Color.White)

            Spacer(modifier = Modifier.height(8.dp))

            Row {
                Button(
                    onClick = { navController.navigate("EditUserScreen/${user.id}") }, // Pasar el ID del usuario
                    modifier = Modifier.weight(1f).shadow(elevation = 8.dp, shape = RoundedCornerShape(40) ).clip(
                        RoundedCornerShape(40)
                    ),
                    colors = ButtonDefaults.buttonColors(containerColor = BrownTertiary)
                ) {
                    Text(stringResource(R.string.edit), color = Color.White)
                }

                Spacer(modifier = Modifier.width(8.dp))

                Button(
                    onClick = { showDialog = true },
                    modifier = Modifier.weight(1f).shadow(elevation = 8.dp, shape = RoundedCornerShape(40) ).clip(
                        RoundedCornerShape(40)
                    ),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) {
                    Text(stringResource(R.string.delete), color = Color.White)
                }

                Spacer(modifier = Modifier.width(8.dp))

                Button(
                    onClick = {
                        coroutineScope.launch {
                            try {
                                val response = if (user.isStatus) userApi.desactivateUserStatus(user.id) else userApi.activateUserStatus(user.id)
                                if (response.isSuccessful) {
                                    val index = users.indexOfFirst { it.id == user.id }
                                    if (index != -1) {
                                        users[index] = User(
                                            users[index].id,
                                            users[index].name,
                                            users[index].email,
                                            users[index].phoneNumber,
                                            !users[index].isStatus,
                                            users[index].role
                                        )
                                    }
                                }
                            } catch (e: Exception) {
                                println("Error: ${e.message}")
                            }
                        }
                    },
                    modifier = Modifier.weight(1f).shadow(elevation = 8.dp, shape = RoundedCornerShape(40) ).clip(
                        RoundedCornerShape(40)
                    ),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.White)
                ) {
                    Text(if (user.isStatus) stringResource(R.string.deactivate) else stringResource(R.string.activate), color = OrangePrimary)
                }
            }
        }
    }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text(stringResource(R.string.confirm_delete), fontWeight = FontWeight.Bold, color = BrownTertiary) },
            text = { Text(stringResource(R.string.confirm_delete_message, user.name), color = Color.Black) },
            confirmButton = {
                Button(
                    onClick = {
                        coroutineScope.launch {
                            try {
                                val response = userApi.deleteUser(user.id)
                                if (response.isSuccessful) {
                                    users.remove(user)
                                }
                            } catch (e: Exception) {
                                println("Error: ${e.message}")
                            }
                        }
                        showDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) {
                    Text(stringResource(R.string.delete), color = Color.White)
                }
            },
            dismissButton = {
                Button(
                    onClick = { showDialog = false },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.LightGray)
                ) {
                    Text(stringResource(R.string.cancel), color = Color.Black)
                }
            }
        )
    }
}