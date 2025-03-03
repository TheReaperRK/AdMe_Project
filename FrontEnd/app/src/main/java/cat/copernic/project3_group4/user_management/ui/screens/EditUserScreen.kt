package cat.copernic.project3_group4.user_management.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import cat.copernic.project3_group4.R
import cat.copernic.project3_group4.core.models.User
import cat.copernic.project3_group4.core.ui.theme.OrangePrimary
import cat.copernic.project3_group4.core.utils.enums.Roles
import cat.copernic.project3_group4.user_management.data.datasource.UserApiRest
import cat.copernic.project3_group4.user_management.data.datasource.UserRetrofitInstance
import kotlinx.coroutines.launch

@Composable
fun EditUserScreen(userId: Long, userState: MutableState<User?>, navController: NavController) {

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
    var selectedRole by remember { mutableStateOf(Roles.USER) }

    if (user == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text(stringResource(R.string.no_authenticated_user), textAlign = TextAlign.Center)
        }
        return
    }

    LaunchedEffect(user.id) {
        coroutineScope.launch {
            try {
                val response = userApi.getUserById(user.id)

                if (response.isSuccessful) {
                    response.body()?.let {
                        name = it.name
                        email = it.email
                        phoneNumber = it.phoneNumber
                        isStatus = it.isStatus
                        selectedRole = it.role
                    }
                } else {
                    Toast.makeText(context, context.getString(R.string.error_loading_user), Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(context, "${context.getString(R.string.error)}: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    Column(modifier = Modifier.fillMaxSize()) {
        // Barra superior fija
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(OrangePrimary)
                .padding(16.dp),
            contentAlignment = Alignment.CenterStart
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = { navController.popBackStack() }) {
                    Icon(
                        imageVector = Icons.Default.KeyboardArrowLeft,
                        contentDescription = stringResource(R.string.back),
                        tint = Color.White
                    )
                }
                Text(stringResource(R.string.edit_user), fontSize = 22.sp, fontWeight = FontWeight.Bold, color = Color.White)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text(stringResource(R.string.name)) },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text(stringResource(R.string.email1)) },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = phoneNumber,
                onValueChange = { phoneNumber = it },
                label = { Text(stringResource(R.string.phone1)) },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = word,
                onValueChange = { word = it },
                label = { Text(stringResource(R.string.password)) },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    text = stringResource(
                        R.string.status_format, // Definido en strings.xml
                        if (isStatus) stringResource(R.string.active) else stringResource(R.string.inactive)
                    )
                )
                Switch(checked = isStatus, onCheckedChange = { isStatus = it })
            }

            Spacer(modifier = Modifier.height(12.dp))

            var expanded by remember { mutableStateOf(false) }
            Box(contentAlignment = Alignment.Center) {
                Button(onClick = { expanded = true }) {
                    Text(stringResource(R.string.role_format, selectedRole.name))
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
                                Toast.makeText(context, context.getString(R.string.user_updated), Toast.LENGTH_SHORT).show()
                                navController.popBackStack()
                            } else {
                                Toast.makeText(context, context.getString(R.string.error_updating), Toast.LENGTH_SHORT).show()
                            }
                        } catch (e: Exception) {
                            Toast.makeText(context, "${context.getString(R.string.error)}: ${e.message}", Toast.LENGTH_SHORT).show()
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = OrangePrimary)
            ) {
                Text(stringResource(R.string.save_changes), color = Color.White)
            }
        }
    }
}