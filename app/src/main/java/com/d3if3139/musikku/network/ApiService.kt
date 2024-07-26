package com.d3if3139.musikku.network

import com.d3if3139.musikku.model.BookTelU
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.MultipartBody
import okhttp3.RequestBody
import com.d3if3139.musikku.model.MessageResponse
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Query

private const val BASE_URL = "https://musicly-app.vercel.app/"


private val moshi = Moshi.Builder()
    .add(KotlinJsonAdapterFactory())
    .build()



private val retrofit = Retrofit.Builder()
    .addConverterFactory(MoshiConverterFactory.create(moshi))
    .baseUrl(BASE_URL)
    .build()


interface UserApi {
    @Multipart
    @POST("musics/")
    suspend fun addData(
        @Part("title") title: RequestBody,
        @Part("genre") genre: RequestBody,
        @Part("description") description: RequestBody,
        @Part("user_email") userEmail: RequestBody,
        @Part file: MultipartBody.Part
    ): BookTelU

    @GET("musics/")
    suspend fun getAllData(
        @Query("email") email: String,
    ): List<BookTelU>

    @DELETE("musics/{music_id}")
    suspend fun deleteData(
        @Path("music_id") id: Int,
        @Query("email") email: String
    ): MessageResponse
}


object Api {
    val userService: UserApi by lazy {
        retrofit.create(UserApi::class.java)
    }

    fun getImageUrl(imageId: String): String{
        return BASE_URL + "musics/images/$imageId"
    }
}

enum class ApiStatus { LOADING, SUCCESS, FAILED }