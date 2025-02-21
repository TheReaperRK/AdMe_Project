package cat.copernic.project3_group4.category_management.data.datasource

import cat.copernic.project3_group4.core.models.Ad
import cat.copernic.project3_group4.core.models.Category
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.*

interface CategoryApiRest {

    @GET("all")
    suspend fun getAllCategories(): Response<List<Category>>

    @GET("byId/{categoryId}")
    suspend fun getCategoryById(@Path("categoryId") categoryId: Long): Response<Category>

    @Multipart
    @POST("create")
    suspend fun createCategory(
        @Part("name") name: RequestBody,
        @Part("description") description: RequestBody,
        @Part image: MultipartBody.Part?,
        @Part("proposal") proposal: RequestBody
    ): Response<Long>

    @Multipart
    @PUT("update")
    suspend fun updateCategory(
        @Part("id") id: RequestBody,
        @Part("name") name: RequestBody,
        @Part("description") description: RequestBody,
        @Part image: MultipartBody.Part?,
        @Part("proposal") proposal: RequestBody
    ):Response<Void>

    @DELETE("delete/{categoryId}")
    suspend fun deleteCategory(@Path("categoryId") categoryId: Long): Response<Void>
}
