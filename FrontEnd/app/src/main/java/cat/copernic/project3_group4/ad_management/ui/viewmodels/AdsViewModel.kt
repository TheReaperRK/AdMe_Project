package cat.copernic.project3_group4.ad_management.ui.viewmodels;

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cat.copernic.project3_group4.ad_management.data.datasource.AdRetrofitInstance
import cat.copernic.project3_group4.ad_management.data.datasource.AdApiRest
import cat.copernic.project3_group4.core.models.Ad
import kotlinx.coroutines.launch
import retrofit2.Response
import android.util.Log
import cat.copernic.project3_group4.core.models.Category


class AdsViewModel : ViewModel() {
    private val adApi: AdApiRest = AdRetrofitInstance.create(AdApiRest::class.java)

    private val _ads = MutableLiveData<List<Ad>>()
    val ads: LiveData<List<Ad>> = _ads

    private val _selectedCategory = MutableLiveData<String?>()
    val selectedCategory: LiveData<String?> =
        _selectedCategory  // ✅ Agregado para que se pueda observar

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
}