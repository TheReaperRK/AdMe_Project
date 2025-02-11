package cat.copernic.project3_group4.user_management.data.datasource

import cat.copernic.project3_group4.core.models.User
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface UserApiRest {

    @GET("all")
    suspend fun getAllUsers(): Response<List<User>>


    @GET("byId/{userId}")
    suspend fun getUserById(@Path("userId") userId: Long): Response<User>


    @POST("create")
    suspend fun createUser(@Body user: User): Response<Long>


    @PUT("update")
    suspend fun updateUser(@Body product: User): Response<Void>


    @DELETE("delete/{userId}")
    suspend fun deleteUser(@Path("userId") userId: Long): Response<Void>
}