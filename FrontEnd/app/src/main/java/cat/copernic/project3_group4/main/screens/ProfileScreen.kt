package cat.copernic.project3_group4.main.screens

import android.graphics.BitmapFactory
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import cat.copernic.project3_group4.core.models.User
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import cat.copernic.project3_group4.core.ui.theme.OrangePrimary
import cat.copernic.project3_group4.core.ui.theme.Project3_Group4Theme
import cat.copernic.project3_group4.core.utils.enums.Roles
import kotlinx.coroutines.launch

@Composable
fun ProfileScreen(userState: MutableState<User?>, navController: NavController) {
    val user = userState.value
    val scope = rememberCoroutineScope()
    val drawerState = rememberDrawerState(DrawerValue.Closed)

    if (user == null) {
        Text("No hay usuario autenticado", textAlign = TextAlign.Center, modifier = Modifier.fillMaxSize())
        return
    }

    val imageBitmap = user.imageBytes?.let { BitmapFactory.decodeByteArray(it, 0, it.size) }
    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.Top
                ) {
                    Text(
                        text = "Menú",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = OrangePrimary
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    DrawerButton("Usuarios") { navController.navigate("user_list") }
                    DrawerButton("Propuestas") { /* Acción de opción 2 */ }
                    DrawerButton("Cerrar sesión") {
                        userState.value = null
                        navController.navigate("login")
                    }
                    DrawerButton("Restablecer contraseña") {
                        navController.navigate("paswordRecover")
                    }
                }
            }
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White) // Fondo blanco
                .padding(0.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.TopEnd
            ) {
                IconButton(
                    onClick = { scope.launch { drawerState.open() } },
                    modifier = Modifier
                        .padding(start = 16.dp, top = 16.dp) // Espaciado desde los bordes
                        .size(56.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Menu,
                        contentDescription = "Menu",
                        tint = OrangePrimary
                    )
                }
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
            Text(text = "Email: ${user.email}", fontSize = 14.sp, color = Color.Gray)

            Spacer(modifier = Modifier.height(6.dp))
            Text(text = "Teléfono: ${user.phoneNumber}", fontSize = 14.sp, color = Color.Gray)

            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = "Estado: ${if (user.isStatus) "Activo" else "Inactivo"}",
                fontSize = 14.sp,
                color = Color.Gray
            )

            Spacer(modifier = Modifier.height(6.dp))
            Text(text = "Rol: ${user.role}", fontSize = 14.sp, color = Color.Gray)


            Spacer(modifier = Modifier.height(24.dp))

            // **Este Spacer empuja el BottomNavigationBar hacia abajo**
            Spacer(modifier = Modifier.weight(1f))

            // **BottomNavigationBar siempre abajo**
            BottomNavigationBar(navController)
        }
    }
}

@Composable
fun DrawerButton(text: String, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(containerColor = OrangePrimary),
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp)
    ) {
        Text(text, color = Color.White)
    }
}




