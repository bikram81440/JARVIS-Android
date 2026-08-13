package com.example.network

import com.squareup.moshi.JsonClass
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import java.util.concurrent.TimeUnit

@JsonClass(generateAdapter = true)
data class ChatRequest(
    val message: String,
    val systemInstruction: String? = null
)

@JsonClass(generateAdapter = true)
data class ChatResponse(
    val success: Boolean,
    val reply: String
)

@JsonClass(generateAdapter = true)
data class HealthResponse(
    val status: String
)

interface BackendApi {
    @GET("health")
    suspend fun healthCheck(): HealthResponse

    @POST("api/chat")
    suspend fun chat(@Body request: ChatRequest): ChatResponse
}

object BackendApiClient {
    private val BASE_URL: String
        get() {
            val url = try {
                com.example.BuildConfig.BACKEND_BASE_URL
            } catch (e: Exception) {
                null
            }
            if (url.isNullOrBlank()) {
                throw IllegalStateException("Backend is not configured.")
            }
            return if (url.endsWith("/")) url else "$url/"
        }

    private val okHttpClient = OkHttpClient.Builder()
        .connectTimeout(60, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(60, TimeUnit.SECONDS)
        .build()

    val service: BackendApi by lazy {
        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(MoshiConverterFactory.create())
            .build()
        retrofit.create(BackendApi::class.java)
    }
}
