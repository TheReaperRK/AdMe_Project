package cat.copernic.project3_group4.ad_management.data.datasource

import cat.copernic.project3_group4.core.models.Ad
import retrofit2.Response
import retrofit2.http.*

interface AdApiRest {
    @GET("all")
    suspend fun getAllAds(): Response<List<Ad>>

    @GET("byId/{adId}")
    suspend fun getAdById(@Path("adId") adId: Long): Response<Ad>

    @POST("create")
    suspend fun createAd(@Body ad: Ad): Response<Long>

    @PUT("update")
    suspend fun updateAd(@Body ad: Ad): Response<Void>

    @DELETE("delete/{adId}")
    suspend fun deleteAd(@Path("adId") adId: Long): Response<Void>

    @GET("byCategory/{categoryId}")
    suspend fun getAdsByCategory(@Path("categoryId") categoryId: Long): Response<List<Ad>>

}
