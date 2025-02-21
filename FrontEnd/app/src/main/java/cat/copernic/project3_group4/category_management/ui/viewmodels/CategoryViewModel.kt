package cat.copernic.project3_group4.category_management.ui.viewmodels

import android.content.ContentValues
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.LiveData
import cat.copernic.project3_group4.core.models.Category
import cat.copernic.project3_group4.category_management.data.datasource.CategoryApiRest
import cat.copernic.project3_group4.category_management.data.datasource.CategoryRetrofitInstance
import okhttp3.MultipartBody
import okhttp3.RequestBody


class CategoryViewModel : ViewModel() {

    private val categoryApi = CategoryRetrofitInstance.retrofitInstance.create(CategoryApiRest::class.java)

    private val _categories = MutableLiveData<List<Category>>()
    val categories: LiveData<List<Category>> = _categories
    private val _category = MutableLiveData<Category>()
    val category: LiveData<Category> get()= _category

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
    fun fetchCategoryById(categoryId: Long) {
        viewModelScope.launch {
            try {
                val response = categoryApi.getCategoryById(categoryId)
                if (response.isSuccessful) {
                    _category.postValue(response.body())
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
    fun updateCategory(categoryId: RequestBody, name: RequestBody, description: RequestBody, image: MultipartBody.Part?,proposal: RequestBody,
                       onSuccess: () -> Unit, onError: (String) -> Unit) {
        viewModelScope.launch {
            try {
                val response = categoryApi.updateCategory(categoryId, name, description, image, proposal)

                if (response.isSuccessful) {
                   fetchCategories()
                    onSuccess()
                    Log.d("UpdateCategory", "✅ Categoria actualizada correctamente")

                } else {
                    val errorBody = response.errorBody()?.string()
                    val errorMessage = "❌ Error al actualizar la categoria: ${response.code()} - $errorBody"
                    Log.e("UpdateCategory", errorMessage)
                    onError(errorMessage)

                }

                    fetchCategories()


            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

}