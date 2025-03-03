package cat.copernic.project3_group4.main.screens

import android.graphics.BitmapFactory
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.outlined.AddCircle
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

import androidx.navigation.compose.rememberNavController
import cat.copernic.project3_group4.R
import cat.copernic.project3_group4.ad_management.ui.screens.base64ToByteArray
import cat.copernic.project3_group4.category_management.ui.viewmodels.CategoryViewModel
import cat.copernic.project3_group4.core.models.Category
import cat.copernic.project3_group4.core.models.User
import coil.compose.AsyncImage

@Composable
fun CategoryScreen(viewModel: CategoryViewModel, userState: MutableState<User?>, navController: NavController) {
    val categories by viewModel.categories.collectAsState()
    var searchText by remember { mutableStateOf("") }
    val user = userState.value
    LaunchedEffect(Unit) {
        viewModel.fetchCategories()
    }

    // Filtra las categorías en función del texto ingresado
    val filteredCategories by remember(searchText, categories) {
        derivedStateOf {
            categories.filter { it.name.startsWith(searchText, ignoreCase = true) }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        TopBar(searchText) { searchText = it }
        FilterButtons(navController, userState)
        CategoryList(filteredCategories, navController, Modifier.weight(1f))
        BottomNavigationBar(navController)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBar(searchText: String, onSearchTextChange: (String) -> Unit) {
    Column {
        TopAppBar(
            title = { Text(stringResource(R.string.search), color = Color.White) },
            colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFFFF6600))
        )
        TextField(
            value = searchText,
            onValueChange = onSearchTextChange,
            placeholder = { Text(stringResource(R.string.search_category)) },
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            singleLine = true
        )
    }
}


@Composable
fun FilterButtons(navController: NavController, userState: MutableState<User?>) {
    var buttonCategoryFormName: String;
    val user = userState.value
    if (user == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text(stringResource(R.string.no_authenticated_user))
        }
        return
    }
    val title = if (user.role.name == "ADMIN") stringResource(R.string.create_category) else stringResource(R.string.proposal)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        val buttonColor = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFAA00))

        Button(onClick = {}, colors = buttonColor) {
            Text(stringResource(R.string.filter))
        }
        Button(onClick = {}, colors = buttonColor) {
            Text(stringResource(R.string.all))
        }
        Button(onClick = { navController.navigate("categoryFormScreen") }, colors = buttonColor) {
            Text(title)
        }
    }
}

@Composable
fun CategoryList(categories: List<Category>, navController: NavController, modifier: Modifier = Modifier) {
    LazyColumn(modifier = modifier.fillMaxWidth()) {
        items(categories) { category ->
            if (!category.isProposal) {
                CategoryItem(category, navController)
            }
        }
    }
}

@Composable
fun CategoryItem(category: Category, navController: NavController) {
    val imageUrl by remember(category.image) { mutableStateOf(base64ToByteArray(category.image)) }
    var expanded by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable { navController.navigate("adsScreen/${category.id}") } // Pasar el ID
    ) {
        AsyncImage(
            model = imageUrl,
            contentDescription = null,
            modifier = Modifier
                .fillMaxWidth()
                .height(150.dp)
                .clip(RoundedCornerShape(12.dp)), // Bordes redondeados
            contentScale = ContentScale.Crop
        )
        Text(
            category.name,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(8.dp)
        )

        Button(onClick = { expanded = !expanded }) {
            Text(if (expanded) "Ver menos" else "Ver más")
        }

        if (expanded) {
            Text(
                text = category.description,
                fontSize = 16.sp,
                modifier = Modifier.padding(8.dp)
            )

        }
    }
}


@Composable
fun BottomNavigationBar(navController: NavController) {
    val currentRoute = navController.currentDestination?.route
    val selectedColor = Color(0xFFFF6600)
    val unselectedColor = Color.White
    NavigationBar(containerColor = Color(0xFFFF6600), modifier = Modifier.height(90.dp)) {
        NavigationBarItem(
            selected = currentRoute == "categoryScreen",
            onClick = { navController.navigate("categoryScreen") },
            icon = {
                Icon(
                    painter = painterResource(
                        if (currentRoute == "categoryScreen") R.drawable.home_icon_filled_orange else R.drawable.home_icon_contourn,
                    ),
                    contentDescription = stringResource(R.string.home),
                    tint = if (currentRoute == "categoryScreen") selectedColor else unselectedColor,
                    modifier = Modifier.size(38.dp)
                )
            },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = selectedColor,
                unselectedIconColor = unselectedColor,
                selectedTextColor = selectedColor,
                unselectedTextColor = unselectedColor
            ),
            modifier = Modifier.padding(top =20.dp)
        )

        NavigationBarItem(
            selected = currentRoute == "AdsScreen",
            onClick = { navController.navigate("AdsScreen") },
            icon = {
                Icon(
                    painter = painterResource(
                        if (currentRoute == "AdsScreen") R.drawable.ad_icon_filled else R.drawable.ad_icon_contourn,
                    ),
                    contentDescription = stringResource(R.string.ads),
                    tint = if (currentRoute == "AdsScreen") selectedColor else unselectedColor,
                    modifier = Modifier.size(49.dp)
                )
            },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = selectedColor,
                unselectedIconColor = unselectedColor,
                selectedTextColor = selectedColor,
                unselectedTextColor = unselectedColor
            ),
            modifier = Modifier.padding(top =20.dp)
        )
        NavigationBarItem(
            selected = currentRoute == "createAdScreen",
            onClick = { navController.navigate("createAdScreen") },
            icon = { Icon(
                painter = painterResource(
                    if (currentRoute == "createAdScreen") R.drawable.a_adir_icon_filled else R.drawable.a_adir_icon_contourn,
                ),
                contentDescription = stringResource(R.string.create),
                tint = if (currentRoute == "createAdScreen") selectedColor else unselectedColor,
                modifier = Modifier.size(42.dp)
            ) },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = selectedColor,
                unselectedIconColor = unselectedColor,
                selectedTextColor = selectedColor,
                unselectedTextColor = unselectedColor
            ),
            modifier = Modifier.padding(top =20.dp)


        )
        NavigationBarItem(
            selected = currentRoute == "profile",
            onClick = { navController.navigate("profile") },
            icon = {  Icon(
                painter = painterResource(
                    if (currentRoute == "profile") R.drawable.profile_icon_filled else R.drawable.profile_icon_contourn,
                ),
                contentDescription = stringResource(R.string.profile),
                tint = if (currentRoute == "profile") selectedColor else unselectedColor,
                modifier = Modifier.size(42.dp)
            )},
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = selectedColor,
                unselectedIconColor = unselectedColor,
                selectedTextColor = selectedColor,
                unselectedTextColor = unselectedColor
            ),
            modifier = Modifier.padding(top =20.dp)

        )
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewCategoryScreen() {
    val viewModel: CategoryViewModel = viewModel()
    val navController = rememberNavController()
    val userState = rememberSaveable { mutableStateOf<User?>(null) }
    CategoryScreen(viewModel = viewModel, userState = userState, navController = navController)
}