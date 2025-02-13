package cat.copernic.project3_group4.user_management.data.datasource

import cat.copernic.project3_group4.core.models.User
import retrofit2.Response
import retrofit2.http.*

interface AuthApiRest {
    @POST("/api/auth/login")
    suspend fun login(
        @Query("email") email: String,
        @Query("password") password: String
    ): Response<User>
}