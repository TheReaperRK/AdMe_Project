package cat.copernic.project3_group4.ad_management.ui.viewmodels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.viewModelScope;
import cat.copernic.project3_group4.ad_management.data.datasource.AdRetrofitInstance;
import cat.copernic.project3_group4.ad_management.data.datasource.AdApiRest;
import cat.copernic.project3_group4.core.models.Ad;
import kotlinx.coroutines.launch;
import retrofit2.Response;

class AdsViewModel : ViewModel() {
    private val adApi: AdApiRest = AdRetrofitInstance.create(AdApiRest::class.java)

    private val _ads: MutableLiveData<List<Ad>> = MutableLiveData()
    val ads: LiveData<List<Ad>> = _ads

    private val _selectedCategory = MutableLiveData<String?>()

    fun fetchAds() {
        viewModelScope.launch {
            try {
                val response: Response<List<Ad>> = adApi.getAllAds()
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
                val response: Response<List<Ad>> = adApi.getAdsByCategory(categoryId)
                if (response.isSuccessful) {
                    _ads.postValue(response.body())
                }
            } catch (e: Exception) {
                println("🚨 Error en fetchAdsByCategory(): ${e.message}")
            }
        }
    }


    fun setSelectedCategory(categoryId: Long) { // Ahora usa Long
        _selectedCategory.value = categoryId.toString()
        fetchAdsByCategory(categoryId)
    }


}
