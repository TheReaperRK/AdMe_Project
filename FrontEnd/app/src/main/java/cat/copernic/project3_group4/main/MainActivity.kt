package cat.copernic.project3_group4.main

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import cat.copernic.project3_group4.core.models.User
import cat.copernic.project3_group4.core.ui.theme.Project3_Group4Theme
import cat.copernic.project3_group4.user_management.data.datasource.UserApiRest
import cat.copernic.project3_group4.user_management.data.datasource.UserRetrofitInstance
import kotlinx.coroutines.launch


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Project3_Group4Theme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    UserListScreen(modifier = Modifier.padding(innerPadding))
                }
            }
        }
    }
}

@Composable
fun UserListScreen(modifier: Modifier = Modifier) {
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
        Text(text = "Total de usuarios: ${users.size}")
        LazyColumn(modifier = Modifier.fillMaxSize()) {
            items(users) { user ->
                UserItem(user)
            }
        }
    }
}

@Composable
fun UserItem(user: User) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = user.name,
                style = MaterialTheme.typography.titleMedium
            )
            Text(text = user.email, style = MaterialTheme.typography.bodyMedium)
            Text(text = "Teléfono: ${user.phoneNumber}", style = MaterialTheme.typography.bodyMedium)
            Text(text = "Estado: ${if (user.isStatus) "Activo" else "Inactivo"}", style = MaterialTheme.typography.bodyMedium)
            Text(text = "Rol: ${user.role}", style = MaterialTheme.typography.bodyMedium)
        }
    }
}
