package cat.copernic.project3_group4.category_management.data.repositories
/*
import cat.copernic.project3_group4.category_management.data.datasource.CategoryApiRest
import cat.copernic.project3_group4.category_management.domain.repositories.CategoryRepository
import cat.copernic.project3_group4.core.models.Category
import cat.copernic.project3_group4.core.utils.createPartFromString
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.Multipart

class CategoryRepositoryImpl(){

}
class CategoryRepositoryImpl(
    private val api: CategoryApiRest
) : CategoryRepository {
    override suspend fun fetchCategory(): Result<List<Category>> {
        TODO("Not yet implemented")
    }

    override suspend fun fetchCategoryById(categoryId: Long): Result<Category> {
        TODO("Not yet implemented")
    }



    override suspend fun updateCategory(category: Category, imageFile: MultipartBody.Part?):Result<Void>{
        TODO("Not yet implemented")
    }
/*
    override suspend fun updateCategory(
        categoryId: Long,
        name: String,
        description: String,
        proposal: Boolean,
        imageByteArray: ByteArray?
    ): Result<Unit> {
        return try {
            val response = api.updateCategory(
                createPartFromString(categoryId.toString()),
                createPartFromString(name),
                createPartFromString(description),
                imageByteArray?.let {
                    val requestBody = RequestBody.create("image/*".toMediaTypeOrNull(), it)
                    MultipartBody.Part.createFormData("image", "category_image.jpg", requestBody)
                },
                createPartFromString(proposal.toString())
            )
            if (response.isSuccessful) Result.success(Unit)
            else Result.failure(Exception(response.message()))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }*/*/

}*/