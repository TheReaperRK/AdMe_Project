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
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import okhttp3.MultipartBody
import okhttp3.RequestBody


class CategoryViewModel : ViewModel() {

    private val categoryApi = CategoryRetrofitInstance.retrofitInstance.create(CategoryApiRest::class.java)

    private val _categories = MutableStateFlow<List<Category>>(emptyList())
    val categories: StateFlow<List<Category>> = _categories
    private val _category = MutableStateFlow<Category>(Category())
    val category: StateFlow<Category> get()= _category
    private val _proposals =  MutableStateFlow<List<Category>>(emptyList())
    val proposals: StateFlow<List<Category>> get()= _proposals
    fun fetchCategories() {
        viewModelScope.launch {
            try {
                val response = categoryApi.getAllCategories()
                if (response.isSuccessful) {
                    _categories.value =(response.body() ?: emptyList())
                    Log.d("FetchCategories", "✅ Categorias obtenidas correctamente")
                }else{
                    Log.e("FetchCategories","❌ Error al obtener las categorias: ${response.code()} - ${response.errorBody()}" )
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
                    _category.value = (response.body()?: Category())
                    Log.d("FetchCategoryById", "✅ Categoria obtenida correctamente")
                }else{
                    Log.e("FetchCategoryById","❌ Error al obtener la categoria: ${response.code()} - ${response.errorBody()}" )
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


            } catch (e: Exception ) {
                e.printStackTrace()
            }
        }
    }

    fun fetchProposals() {
        viewModelScope.launch {
            try {
                val response = categoryApi.getAllProposals()
                if (response.isSuccessful) {
                    _proposals.value = (response.body() ?: emptyList())
                    Log.d("FetchProposals", "✅ Propuestas obtenidas correctamente")
                }else{
                    Log.e("FetchProposals","❌ Error al obtener las propuestas: ${response.code()} - ${response.errorBody()}" )
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
    fun deleteCategoryById(categoryId: Long) {
        viewModelScope.launch {
            try {
                val response = categoryApi.deleteCategory(categoryId)
                if (response.isSuccessful) {
                    Log.d("DeleteCategory", "✅ Categoria eliminada correctamente")
                }else{
                    val errorBody = response.errorBody()?.string()
                    val errorMessage = "❌ Error al eliminar la categoria: ${response.code()} - $errorBody"
                    Log.e("DeleteCategory", errorMessage)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun acceptProposal(categoryId: Long) {
        viewModelScope.launch {
            try {
                val response = categoryApi.acceptProposal(categoryId)
                if (response.isSuccessful) {
                    Log.d("AcceptProposal", "✅ Propuesta de categoria acceptada correctamente")
                }else{
                    val errorBody = response.errorBody()?.string()
                    val errorMessage = "❌ Error al acceptar la propuesta de categoria: ${response.code()} - $errorBody"
                    Log.e("AcceptProposal", errorMessage)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }


}