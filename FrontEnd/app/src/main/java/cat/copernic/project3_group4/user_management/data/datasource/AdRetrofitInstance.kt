package cat.copernic.project3_group4.user_management.data.datasource

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object AdRetrofitInstance {
    private const val BASE_URL = "http://10.0.2.2:8080/rest/ads/"



    val retrofitInstance: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    fun <T> create(service: Class<T>): T {
        return retrofitInstance.create(service)
    }
}
