package com.example.workly.network

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path

data class CepResponse(
    val cep: String = "",
    val logradouro: String = "",
    val bairro: String = "",
    val localidade: String = "",
    val uf: String = ""
)

interface ViaCepService {
    @GET("ws/{cep}/json/")
    suspend fun getAddress(@Path("cep") cep: String): CepResponse
}

object RetrofitClient {
    private const val BASE_URL = "https://viacep.com.br/"

    val viaCepService: ViaCepService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ViaCepService::class.java)
    }
}
