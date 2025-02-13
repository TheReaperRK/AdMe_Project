package cat.copernic.project3_group4.main.screens

import android.graphics.BitmapFactory
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import cat.copernic.project3_group4.core.models.User
import androidx.compose.ui.text.style.TextAlign

@Composable
fun ProfileScreen(userState: MutableState<User?>, navController: NavController) {
    val user = userState.value

    if (user == null) {
        Text("No hay usuario autenticado", textAlign = TextAlign.Center, modifier = Modifier.fillMaxSize())
        return
    }

    val imageBitmap = user.imageBytes?.let { BitmapFactory.decodeByteArray(it, 0, it.size) }
    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White) //
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Button(
                onClick = { navController.navigate("user_list") },
                modifier = Modifier
                    .padding(16.dp)
                    .size(48.dp)
                    .align(Alignment.Start),
                shape = CircleShape,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFA500))
            ) {
                Text("<", color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold)
            }
            Spacer(modifier = Modifier.height(20.dp))
            imageBitmap?.let {
                Image(
                    bitmap = it.asImageBitmap(),
                    contentDescription = "Profile Image",
                    modifier = Modifier
                        .size(100.dp)
                        .clip(CircleShape),
                    contentScale = ContentScale.Crop
                )
            }

            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = user.name,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )

            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = "Email: ${user.email}",
                fontSize = 14.sp,
                color = Color.Gray
            )

            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = "Teléfono: ${user.phoneNumber}",
                fontSize = 14.sp,
                color = Color.Gray
            )

            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = "Estado: ${if (user.isStatus) "Activo" else "Inactivo"}",
                fontSize = 14.sp,
                color = Color.Gray
            )

            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = "Rol: ${user.role}",
                fontSize = 14.sp,
                color = Color.Gray
            )

            Spacer(modifier = Modifier.height(12.dp))
            Button(
                onClick = { /* Acción de restablecer contraseña */ },
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth(0.6f),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFA500)) // Naranja
            ) {
                Text("Restablecer contraseña", color = Color.White)
            }

            Spacer(modifier = Modifier.height(24.dp))
            Button(
                onClick = {
                    userState.value = null
                    navController.navigate("login")
                },
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth(0.6f),
                colors = ButtonDefaults.buttonColors(containerColor = Color.Black)
            ) {
                Text("Cerrar sesión", color = Color.White)
            }


        }
    }
}