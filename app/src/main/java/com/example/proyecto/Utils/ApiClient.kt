package com.example.proyecto.Utils

import android.content.Context
import com.example.proyecto.R
import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.POST

interface ApiService {
    @POST("/upimg/")
    fun postData(@Body requestBody: RequestBody): Call<ApiResponse>
}

data class ApiResponse(
    val message: String,
    val url: String
)

data class RequestBody(
    val img: String,
    val apiKey: String
)

object RetrofitClient {
    private const val BASE_URL = "https://compactmcbe.online/"

    private val gson = GsonBuilder()
        .setLenient()
        .create()

    private val client = OkHttpClient.Builder().build()

    val instance: ApiService by lazy {
        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()

        retrofit.create(ApiService::class.java)
    }
}

fun fetchData(context: Context, img: String, onComplete: (String?) -> Unit) {
    val apiKey = context.getString(R.string.api_key_img)
    val requestBody = RequestBody(img, apiKey)
    RetrofitClient.instance.postData(requestBody).enqueue(object : retrofit2.Callback<ApiResponse> {
        override fun onResponse(call: Call<ApiResponse>, response: retrofit2.Response<ApiResponse>) {
            if (response.isSuccessful) {
                val imageUrl = response.body()?.url
                onComplete(imageUrl)
            } else {
                println("Error en la respuesta: ${response.errorBody()}")
                onComplete(null)
            }
        }

        override fun onFailure(call: Call<ApiResponse>, t: Throwable) {
            println("Error en la solicitud: ${t.message}")
            onComplete(null)
        }
    })
}