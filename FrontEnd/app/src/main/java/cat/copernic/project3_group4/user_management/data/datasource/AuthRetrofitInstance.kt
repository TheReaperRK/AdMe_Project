package cat.copernic.project3_group4.user_management.data.datasource

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object AuthRetrofitInstance {
    private val retrofit by lazy {
        Retrofit.Builder()
            .baseUrl("http://10.0.2.2:8080/api/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val authApi: AuthApiRest by lazy {
        retrofit.create(AuthApiRest::class.java)
    }
}