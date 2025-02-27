package cat.copernic.project3_group4.user_management.data.datasource

import cat.copernic.project3_group4.core.models.User
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Part
import retrofit2.http.Path

interface UserApiRest {

    @GET("all")
    suspend fun getAllUsers(): Response<List<User>>

    @GET("byId/{userId}")
    suspend fun getUserById(@Path("userId") userId: Long): Response<User>

    @POST("create")
    suspend fun createUser(@Body user: User): Response<Long>

    @DELETE("delete/{userId}")
    suspend fun deleteUser(@Path("userId") userId: Long): Response<Void>

    @PUT("activate/{userId}")
    suspend fun activateUserStatus(@Path("userId") userId: Long): Response<Void>

    @PUT("desactivate/{userId}")
    suspend fun desactivateUserStatus(@Path("userId") userId: Long): Response<Void>

    @PUT("update/{userId}")
    suspend fun updateUser(@Path("userId") userId: Long, @Body updatedUser: User): Response<User>

    @PUT("expireWord/{userId}")
    suspend fun expireWord(@Path("userId") userId: Long): Response<User>
}