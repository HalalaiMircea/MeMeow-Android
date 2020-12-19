package ro.unibuc.cs.memeow.api

import okhttp3.MultipartBody
import retrofit2.Call
import retrofit2.http.*
import ro.unibuc.cs.memeow.model.*

interface MemeowApi {

    @GET("templates/available")
    suspend fun getAvailableTemplates(
        @Query("contains") contains: String?,
        @Query("minLevel") minLevel: Int?,
        @Query("page") page: Int,
        @Query("pageSize") pageSize: Int
    ): List<MemeTemplate>

    @GET("templates/unavailable")
    suspend fun getUnavailableTemplates(
        @Query("contains") contains: String?,
        @Query("page") page: Int,
        @Query("pageSize") pageSize: Int
    ): List<MemeTemplate>

    @GET("profile/own")
    fun getOwnProfile(): Call<Profile>

    @GET("profile/{id}")
    fun getUserProfile(@Path("id") uuid: String): Call<Profile>

    @POST("authenticate/facebook")
    fun facebookAuth(@Body body: FacebookAuthUser): Call<ServerAuthResponse>

    @Multipart
    @POST("memes/create")
    fun uploadMeme(
        @Part image: MultipartBody.Part, @Part("templateName") templateId: String
    ): Call<PostedMeme>

    companion object {
        const val BASE_URL = "https://memeow-dev.herokuapp.com/api/"
    }
}
