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
                println("🚨 Error en fetchAds(): ${e.message}")
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
                println("🚨 Error en fetchAdsByCategory(): ${e.message}")
            }
        }
    }

    fun fetchFilteredAds(categoryIds: Set<Long>, minPrice: Double, maxPrice: Double) {
        viewModelScope.launch {
            try {
                val filteredAds = mutableListOf<Ad>()

                if (categoryIds.isNotEmpty()) {
                    for (categoryId in categoryIds) {
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
                println("🚨 Error en fetchFilteredAds(): ${e.message}")
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
                println("🚨 Error en fetchCategories(): ${e.message}")
            }
        }
    }

    fun createAd(ad: Ad) {
        viewModelScope.launch {
            try {
                val response = adApi.createAd(ad)
                if (response.isSuccessful) {
                    Log.d("CreateAdScreen", "Anuncio creado correctamente: ${response.body()}")
                } else {
                    val errorBody = response.errorBody()?.string()
                    Log.e(
                        "CreateAdScreen",
                        "Error al crear el anuncio: ${response.code()} - $errorBody"
                    )
                }

            } catch (e: Exception) {
                println("🚨 Error en createAd(): ${e.message}")
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
                println("🚨 Error en fetchAdsByUser(): ${e.message}")
            }
        }
    }

}
