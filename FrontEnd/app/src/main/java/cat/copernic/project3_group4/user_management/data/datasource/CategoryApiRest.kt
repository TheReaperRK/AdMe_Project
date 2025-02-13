package cat.copernic.project3_group4.category_management.data.datasource

import cat.copernic.project3_group4.core.models.Category
import retrofit2.Response
import retrofit2.http.*

interface CategoryApiRest {

    @GET("all")
    suspend fun getAllCategories(): Response<List<Category>>

    @GET("byId/{categoryId}")
    suspend fun getCategoryById(@Path("categoryId") categoryId: Long): Response<Category>

    @POST("create")
    suspend fun createCategory(@Body category: Category): Response<Long>

    @PUT("update")
    suspend fun updateCategory(@Body category: Category): Response<Void>

    @DELETE("delete/{categoryId}")
    suspend fun deleteCategory(@Path("categoryId") categoryId: Long): Response<Void>
}
