package cat.copernic.project3_group4.main.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color.Companion.Black
import androidx.compose.ui.graphics.Color.Companion.White
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import cat.copernic.project3_group4.core.models.User

@Composable
fun ProfileScreen(userState: MutableState<User?>, navController: NavController) {
    val user = userState.value

    if (user == null) {
        Text("No hay usuario autenticado")
        return
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "Perfil de ${user.name}", style = MaterialTheme.typography.headlineSmall)
        Spacer(modifier = Modifier.height(16.dp))
        Text(text = "Email: ${user.email}")
        Text(text = "Teléfono: ${user.phoneNumber}")
        Text(text = "Estado: ${if (user.isStatus) "Activo" else "Inactivo"}")
        Text(text = "Rol: ${user.role}")

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                userState.value = null
                navController.navigate("login")
            },
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = Black)
        ) {
            Text("Cerrar sesión", color = White)
        }
    }
}
