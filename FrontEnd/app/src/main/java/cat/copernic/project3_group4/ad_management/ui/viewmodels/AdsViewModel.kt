package cat.copernic.project3_group4.ad_management.ui.viewmodels


import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cat.copernic.project3_group4.ad_management.data.datasource.AdApiRest
import cat.copernic.project3_group4.core.models.Ad
import cat.copernic.project3_group4.core.models.Category

import cat.copernic.project3_group4.ad_management.data.datasource.AdRetrofitInstance
import kotlinx.coroutines.launch

class AdsViewModel : ViewModel() {
    private val adApi: AdApiRest = AdRetrofitInstance.create(AdApiRest::class.java)

    private val _ads = MutableLiveData<List<Ad>>()
    val ads: LiveData<List<Ad>> = _ads

    private val _ad = MutableLiveData<Ad?>()
    val ad: LiveData<Ad?> = _ad

    private val _categories = MutableLiveData<List<Category>>()
    val categories: LiveData<List<Category>> = _categories

    private val _selectedCategory = MutableLiveData<String?>()
    val selectedCategory: LiveData<String?> = _selectedCategory

    fun fetchAds() {
        viewModelScope.launch {
            try {
                val response = adApi.getAllAds()
                if (response.isSuccessful) {
                    _ads.postValue(response.body())
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
                    _ad.postValue(response.body())
                } else {
                    Log.e("AdsViewModel", "❌ Error al obtener el anuncio: ${response.errorBody()?.string()}")
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
                    _ads.postValue(response.body())
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
                        }
                    }
                } else {
                    val response = adApi.getAdsByPriceRange(minPrice, maxPrice)
                    if (response.isSuccessful) {
                        response.body()?.let { filteredAds.addAll(it) }
                    }
                }

                _ads.postValue(filteredAds)
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
                    _categories.postValue(response.body())
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
                    fetchAds() // Actualiza la lista de anuncios tras la modificación
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
                    _ads.postValue(response.body())
                }
            } catch (e: Exception) {
                Log.e("AdsViewModel", "🚨 Error en fetchAdsByUser(): ${e.message}")
            }
        }
    }

    fun deleteAd(adId: String, onSuccess: () -> Unit, onError: (String) -> Unit) {
        viewModelScope.launch {
            try {
                val adIdLong = adId.toLongOrNull()
                if (adIdLong == null) {
                    onError("❌ ID de anuncio inválido")
                    return@launch
                }

                val response = adApi.deleteAd(adIdLong)
                if (response.isSuccessful) {
                    _ads.value = _ads.value?.filter { it.id != adIdLong }
                    onSuccess()
                    Log.d("DeleteAd", "✅ Anuncio eliminado correctamente")
                } else {
                    val errorMessage = "❌ Error al eliminar el anuncio"
                    Log.e("DeleteAd", errorMessage)
                    onError(errorMessage)
                }
            } catch (e: Exception) {
                val exceptionMessage = "🚨 Error en deleteAd(): ${e.message}"
                Log.e("DeleteAd", exceptionMessage)
                onError(exceptionMessage)
            }
        }
    }
}
