package cat.copernic.project3_group4.ad_management.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import cat.copernic.project3_group4.ad_management.ui.viewmodels.AdsViewModel
import cat.copernic.project3_group4.core.models.Ad
import cat.copernic.project3_group4.core.models.Category
import coil.compose.AsyncImage
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import cat.copernic.project3_group4.core.ui.theme.OrangePrimary
import cat.copernic.project3_group4.main.screens.BottomNavigationBar
import androidx.compose.ui.res.stringResource
import cat.copernic.project3_group4.R
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.input.ImeAction

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdsScreen(adsViewModel: AdsViewModel, navController: NavController) {
    var selectedCategories by remember { mutableStateOf(setOf<Long>()) }
    var maxPrice by remember { mutableStateOf(10000f) }
    var searchText by remember { mutableStateOf("") }
    var sortOption by remember { mutableStateOf("Default") }
    val ads by adsViewModel.ads.observeAsState(initial = emptyList())
    val categories by adsViewModel.categories.observeAsState(initial = emptyList())

    LaunchedEffect(Unit) {
        adsViewModel.fetchCategories()
    }

    LaunchedEffect(selectedCategories, maxPrice, searchText, sortOption) {
        adsViewModel.fetchFilteredAds(selectedCategories, 0.0, maxPrice.toDouble())
    }

    val filteredAds = ads.filter { it.title.startsWith(searchText, ignoreCase = true) }
        .let {
            when (sortOption) {
                "Title" -> it.sortedBy { ad -> ad.title }
                "Price Asc" -> it.sortedBy { ad -> ad.price }
                "Price Desc" -> it.sortedByDescending { ad -> ad.price }
                else -> it
            }
        }

    Column(modifier = Modifier.fillMaxSize().background(Color.White)) {
        SmallTopAppBar(
            title = { Text(stringResource(R.string.ads), color = Color.White) },
            colors = TopAppBarDefaults.smallTopAppBarColors(containerColor = Color(0xFFFF6600))
        )

        SearchBar(searchText, onSearchTextChanged = { searchText = it })
        FilterSection(categories, selectedCategories, maxPrice, onCategorySelected = {
            selectedCategories = it
        }, onPriceChanged = {
            maxPrice = it
        }, onSortOptionSelected = { sortOption = it })

        Spacer(modifier = Modifier.height(8.dp))

        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(filteredAds) { ad -> AdItem(ad) }
        }

        BottomNavigationBar(navController)
    }
}

@Composable
fun SearchBar(searchText: String, onSearchTextChanged: (String) -> Unit) {
    TextField(
        value = searchText,
        onValueChange = onSearchTextChanged,
        placeholder = { Text("Buscar...") },
        singleLine = true,
        modifier = Modifier.fillMaxWidth().padding(16.dp),
        keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Search),
        keyboardActions = KeyboardActions(onSearch = { /* Handle search */ })
    )
}


@Composable
fun FilterSection(
    categories: List<Category>,
    selectedCategories: Set<Long>,
    maxPrice: Float,
    onCategorySelected: (Set<Long>) -> Unit,
    onPriceChanged: (Float) -> Unit,
    onSortOptionSelected: (String) -> Unit
) {
    Column(modifier = Modifier.padding(16.dp)) {
        Text("Ordenar por:", fontSize = 16.sp, fontWeight = FontWeight.Bold)
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Button(onClick = { onSortOptionSelected("Title") }) { Text("Título") }
            Button(onClick = { onSortOptionSelected("Price Asc") }) { Text("Precio ↑") }
            Button(onClick = { onSortOptionSelected("Price Desc") }) { Text("Precio ↓") }
        }

        Spacer(modifier = Modifier.height(16.dp))
        Text(stringResource(R.string.filter_by_category), fontSize = 16.sp, fontWeight = FontWeight.Bold)
        LazyRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(categories) { category ->
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(4.dp)) {
                    Checkbox(
                        checked = selectedCategories.contains(category.id),
                        onCheckedChange = {
                            val newSelection = selectedCategories.toMutableSet()
                            if (it) newSelection.add(category.id) else newSelection.remove(category.id)
                            onCategorySelected(newSelection)
                        }
                    )
                    Text(category.name)
                }
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
        Text("Precio máximo: ${maxPrice.toInt()}€", fontWeight = FontWeight.Bold)
        Slider(value = maxPrice, onValueChange = onPriceChanged, valueRange = 0f..10000f)
    }
}


@Composable
fun AdItem(ad: Ad) {
    val imageUrl by remember(ad.data) { mutableStateOf(base64ToByteArray(ad.data)) }
    val author = ad.author

    Card(
        modifier = Modifier.fillMaxWidth().padding(8.dp),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = OrangePrimary)
    ) {
        Column {
            AsyncImage(
                model = imageUrl,
                contentDescription = null,
                modifier = Modifier.fillMaxWidth().height(150.dp).clip(RoundedCornerShape(0.dp, 0.dp, 24.dp, 24.dp)),
                contentScale = ContentScale.Crop
            )
            Column(modifier = Modifier.padding(8.dp)) {
                Text(ad.title, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                Text(ad.description, fontSize = 14.sp, maxLines = 2)
                Text("${ad.price}€", fontSize = 14.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(8.dp))
                Divider(color = Color.Black, thickness = 1.dp)
                Text(stringResource(R.string.seller_info), fontWeight = FontWeight.Bold, fontSize = 14.sp, modifier = Modifier.padding(vertical = 4.dp))
                author?.let {
                    Text(stringResource(R.string.seller_name, it.name), fontSize = 12.sp)
                    Text(stringResource(R.string.seller_email, it.email), fontSize = 12.sp)
                    Text(stringResource(R.string.seller_phone, it.phoneNumber), fontSize = 12.sp)
                } ?: Text(stringResource(R.string.no_user_info), fontSize = 12.sp, color = Color.Gray)
            }
        }
    }
}

