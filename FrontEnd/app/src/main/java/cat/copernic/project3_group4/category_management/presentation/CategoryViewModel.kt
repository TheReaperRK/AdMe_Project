package cat.copernic.project3_group4.category_management.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.LiveData
import cat.copernic.project3_group4.core.models.Category
import cat.copernic.project3_group4.category_management.data.datasource.CategoryApiRest
import cat.copernic.project3_group4.user_management.data.datasource.CategoryRetrofitInstance

class CategoryViewModel : ViewModel() {

    private val categoryApi = CategoryRetrofitInstance.retrofitInstance.create(CategoryApiRest::class.java)

    private val _categories = MutableLiveData<List<Category>>()
    val categories: LiveData<List<Category>> = _categories

    fun fetchCategories() {
        viewModelScope.launch {
            try {
                val response = categoryApi.getAllCategories()
                if (response.isSuccessful) {
                    _categories.postValue(response.body() ?: emptyList())
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}