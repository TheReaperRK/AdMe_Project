package cat.copernic.project3_group4.category_management.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.paddingFromBaseline
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.webkit.internal.ApiFeature.M
import cat.copernic.project3_group4.category_management.ui.viewmodels.CategoryViewModel
import cat.copernic.project3_group4.core.models.Category
import cat.copernic.project3_group4.core.models.User
import cat.copernic.project3_group4.core.ui.theme.BrownTertiary
import cat.copernic.project3_group4.core.ui.theme.OrangePrimary
import cat.copernic.project3_group4.main.screens.BottomNavigationBar
import cat.copernic.project3_group4.user_management.data.datasource.UserApiRest
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProposalsScreen(
    categoryViewModel: CategoryViewModel,
    userState: MutableState<User?>,
    navController: NavController
) {

    val coroutineScope = rememberCoroutineScope()
    val proposals by categoryViewModel.proposals.collectAsState()
    LaunchedEffect(Unit) {
        coroutineScope.launch {
            categoryViewModel.fetchProposals()


        }
    }
    LaunchedEffect(proposals) {
        categoryViewModel.fetchProposals()
    }
    Scaffold(
        bottomBar = { BottomNavigationBar(navController) }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White) // Fondo general blanco
                .paddingFromBaseline(0.dp, paddingValues.calculateBottomPadding())
        ) {
            // CABECERA NARANJA
           /* Box(
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

            }*/
            TopAppBar(
                navigationIcon = {

                    IconButton(
                        onClick = { navController.navigate("profile") }
                    ) {
                        Icon(
                            imageVector = Icons.Default.KeyboardArrowLeft,
                            contentDescription = "Volver",
                            tint = Color.White
                        )
                    }

                },
                title = {

                    Text("Propostes de Categoria: ${proposals?.size}", color = Color.White,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold)




                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFFFF6600))
            )

            LazyColumn(modifier = Modifier.fillMaxSize()) {
                items(proposals) { proposal ->
                    ProposalItem(proposal, navController, categoryViewModel)
                    }

            }
        }
    }
}

@Composable
fun ProposalItem(proposal: Category, navController: NavController, categoryViewModel: CategoryViewModel) {
    val coroutineScope = rememberCoroutineScope()
    var showDialog by remember { mutableStateOf(false) }
    var showDialogProposal by remember {mutableStateOf(false)}
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        colors = CardDefaults.cardColors(containerColor = OrangePrimary) // Fondo marrón
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = proposal.name, fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.White)
            Text(text = "Descripcion: ${proposal.description}", fontSize = 14.sp, color = Color.White)


            Spacer(modifier = Modifier.height(8.dp))

            Row {


                Button(

                    onClick = {navController.navigate("editCategoryScreen/${proposal.id}")},
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(containerColor = BrownTertiary) // Botón naranja
                ) {
                    Text("Editar", color = Color.White)
                }

                Spacer(modifier = Modifier.width(8.dp))

                Button(
                    onClick = { showDialog = true
                       },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("Eliminar", color = Color.White)
                }

                Spacer(modifier = Modifier.width(8.dp))

                Button(
                    onClick = {
                        coroutineScope.launch {



                            showDialogProposal = true;
                            categoryViewModel.fetchProposals()
                        }
                    },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.White)
                ) {
                    Text("Acceptar" , color = OrangePrimary)
                }


            }
        }
    }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text("Confirmar eliminación", fontWeight = FontWeight.Bold, color = BrownTertiary) },
            text = { Text("¿Seguro que quieres eliminar a ${proposal.name}? Esta acción no se puede deshacer.", color = Color.Black) },
            confirmButton = {
                Button(
                    onClick = {
                        coroutineScope.launch {
                            try {
                                val response = categoryViewModel.deleteCategoryById(proposal.id)


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


    if (showDialogProposal) {
        AlertDialog(
            onDismissRequest = { showDialogProposal = false },
            title = { Text("Confirmar acceptar propuesta ", fontWeight = FontWeight.Bold, color = BrownTertiary) },
            text = { Text("¿Seguro que quieres acceptar la propuesta  ${proposal.name}? Esta se mostrara publicamente a partir de ahora.", color = Color.Black) },
            confirmButton = {
                Button(
                    onClick = {
                        coroutineScope.launch {
                            try {
                                val response = categoryViewModel.acceptProposal(proposal.id)


                            } catch (e: Exception) {
                                println("Error: ${e.message}")
                            }
                        }

                        showDialogProposal = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("Acceptar", color = Color.White)
                }
            },
            dismissButton = {
                Button(
                    onClick = { showDialogProposal = false },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.LightGray)
                ) {
                    Text("Cancelar", color = Color.Black)
                }
            }
        )
    }

}


