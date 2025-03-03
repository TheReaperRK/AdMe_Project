package cat.copernic.project3_group4.ad_management.ui.viewmodels


import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cat.copernic.project3_group4.ad_management.data.datasource.AdApiRest
import cat.copernic.project3_group4.core.models.Ad
import cat.copernic.project3_group4.core.models.Category
import cat.copernic.project3_group4.ad_management.data.datasource.AdRetrofitInstance
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class AdsViewModel : ViewModel() {
    private val adApi: AdApiRest = AdRetrofitInstance.create(AdApiRest::class.java)

    private val _ads = MutableStateFlow<List<Ad>>(emptyList())
    val ads: StateFlow<List<Ad>> get() = _ads

    private val _ad = MutableStateFlow<Ad?>(null)
    val ad: StateFlow<Ad?> get() = _ad

    private val _categories = MutableStateFlow<List<Category>>(emptyList())
    val categories: StateFlow<List<Category>> get() = _categories

    private val _selectedCategory = MutableStateFlow<String?>(null)
    val selectedCategory: StateFlow<String?> get() = _selectedCategory

    fun fetchAds() {
        viewModelScope.launch {
            try {
                val response = adApi.getAllAds()
                if (response.isSuccessful) {
                    _ads.value = response.body() ?: emptyList()
                }
            } catch (e: Exception) {
                Log.e("AdsViewModel", "🚨 Error en fetchAds(): ${e.message}")
            }
        }
    }

    fun fetchAdById(adId: Long) {
        viewModelScope.launch {
            try {
                val response = adApi.getAdById(adId)
                if (response.isSuccessful) {
                    _ad.value = response.body()
                } else {
                    Log.e("FetchAdsById", "❌ Error al obtener los anuncios: ${response.errorBody()?.string()}")
                }
            } catch (e: Exception) {
                Log.e("AdsViewModel", "🚨 Error en fetchAdById(): ${e.message}")
            }
        }
    }

    fun fetchAdsByCategory(categoryId: Long) {
        viewModelScope.launch {
            try {
                val response = adApi.getAdsByCategory(categoryId)
                if (response.isSuccessful) {
                    _ads.value = response.body() ?: emptyList()
                }
            } catch (e: Exception) {
                Log.e("AdsViewModel", "🚨 Error en fetchAdsByCategory(): ${e.message}")
            }
        }
    }

    fun fetchFilteredAds(categoryIds: Set<Long>, minPrice: Double, maxPrice: Double) {
        viewModelScope.launch {
            try {
                val filteredAds = mutableListOf<Ad>()

                if (categoryIds.isNotEmpty()) {
                    categoryIds.forEach { categoryId ->
                        val response = adApi.getAdsFiltered(categoryId, minPrice, maxPrice)
                        if (response.isSuccessful) {
                            response.body()?.let { filteredAds.addAll(it) }
                            Log.d("fetchFilteredAds", "✅ Anucnios filtrados obtenidos  correctamente")
                        } else {
                            Log.e("fetchFilteredAds", "❌ Error al obtener los anuncios filtrados: ${response.errorBody()?.string()}")
                        }
                    }
                } else {
                    val response = adApi.getAdsByPriceRange(minPrice, maxPrice)
                    if (response.isSuccessful) {
                        response.body()?.let { filteredAds.addAll(it) }
                        Log.e("fetchFilteredAdsByPrice", "✅ Anucnios filtrados por precio obtenidos  correctamente")
                    }else{
                        Log.e("fetchFilteredAdsByPrice", "❌ Error al obtener los anuncios filtrados por precio: ${response.errorBody()?.string()}")
                    }
                }

                _ads.value = filteredAds
            } catch (e: Exception) {
                Log.e("AdsViewModel", "🚨 Error en fetchFilteredAds(): ${e.message}")
            }
        }
    }

    fun fetchCategories() {
        viewModelScope.launch {
            try {
                val response = adApi.getCategories()
                if (response.isSuccessful) {
                    _categories.value = response.body() ?: emptyList()
                }
            } catch (e: Exception) {
                Log.e("AdsViewModel", "🚨 Error en fetchCategories(): ${e.message}")
            }
        }
    }

    fun createAd(ad: Ad) {
        viewModelScope.launch {
            try {
                val response = adApi.createAd(ad)
                if (response.isSuccessful) {
                    Log.d("CreateAdScreen", "✅ Anuncio creado correctamente: ${response.body()}")
                } else {
                    val errorBody = response.errorBody()?.string()
                    Log.e("CreateAdScreen", "❌ Error al crear el anuncio: ${response.code()} - $errorBody")
                }
            } catch (e: Exception) {
                Log.e("AdsViewModel", "🚨 Error en createAd(): ${e.message}")
            }
        }
    }

    fun updateAd(ad: Ad, onSuccess: () -> Unit, onError: (String) -> Unit) {
        viewModelScope.launch {
            try {
                val response = adApi.updateAd(ad)
                if (response.isSuccessful) {
                    fetchAds()
                    fetchAdById(ad.id)
                    onSuccess()
                    Log.d("UpdateAd", "✅ Anuncio actualizado correctamente")
                } else {
                    val errorBody = response.errorBody()?.string()
                    val errorMessage = "❌ Error al actualizar el anuncio: ${response.code()} - $errorBody"
                    Log.e("UpdateAd", errorMessage)
                    onError(errorMessage)
                }
            } catch (e: Exception) {
                val exceptionMessage = "🚨 Error en updateAd(): ${e.message}"
                Log.e("UpdateAd", exceptionMessage)
                onError(exceptionMessage)
            }
        }
    }

    fun setSelectedCategory(categoryId: Long) {
        _selectedCategory.value = categoryId.toString()
        fetchAdsByCategory(categoryId)
    }

    fun fetchAdsByUser(userId: String) {
        viewModelScope.launch {
            try {
                val response = adApi.getAdsByUser(userId)
                if (response.isSuccessful) {
                    _ads.value = response.body() ?: emptyList()
                }
            } catch (e: Exception) {
                Log.e("AdsViewModel", "🚨 Error en fetchAdsByUser(): ${e.message}")
            }
        }
    }

    fun deleteAd(adId: Long, onSuccess: () -> Unit, onError: (String) -> Unit) {
        viewModelScope.launch {
            try {
                val response = adApi.deleteAd(adId)
                if (response.isSuccessful) {
                    fetchAds()
                    onSuccess()
                    Log.d("deleteAd", "✅ Anucnio eliminado  correctamente")
                } else {
                    onError("❌ Error al eliminar el anuncio: ${response.code()}")
                    Log.e("deleteAd", "\"❌ Error al eliminar el anuncio: ${response.errorBody()?.string()}")
                }
            } catch (e: Exception) {
                onError("🚨 Error en deleteAd(): ${e.message}")
                println("${e.message}")
            }
        }
    }
}
