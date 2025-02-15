package cat.copernic.project3_group4.user_management.data.datasource

import cat.copernic.project3_group4.core.models.User
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.*

interface AuthApiRest {
    @POST("/api/auth/login")
    suspend fun login(
        @Query("email") email: String,
        @Query("password") password: String
    ): Response<User>

    @Multipart
    @POST("/api/auth/register")
    suspend fun register(
        @Part("name") name: RequestBody,
        @Part("email") email: RequestBody,
        @Part("phone") phone: RequestBody,
        @Part("password") password: RequestBody,
        @Part image: MultipartBody.Part?
    ): Response<User>

}