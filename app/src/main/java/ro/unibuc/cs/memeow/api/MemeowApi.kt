package ro.unibuc.cs.memeow.api

import okhttp3.MultipartBody
import retrofit2.Call
import retrofit2.http.*
import ro.unibuc.cs.memeow.model.FacebookAuthUser
import ro.unibuc.cs.memeow.model.MemeTemplate
import ro.unibuc.cs.memeow.model.ServerAuthResponse

interface MemeowApi {

    @GET("api/templates/available")
    suspend fun getAvailableTemplates(
        @Query("contains") contains: String?,
        @Query("minLevel") minLevel: Int?,
        @Query("page") page: Int,
        @Query("pageSize") pageSize: Int
    ): List<MemeTemplate>

    @GET("api/templates/unavailable")
    suspend fun getUnavailableTemplates(
        @Query("contains") contains: String?,
        @Query("page") page: Int,
        @Query("pageSize") pageSize: Int
    ): List<MemeTemplate>

    @POST("api/authenticate/facebook")
    fun facebookAuth(@Body body: FacebookAuthUser): Call<ServerAuthResponse>

    @Multipart
    @POST("api/memes/create")
    fun uploadMeme(@Part image: MultipartBody.Part, @Part("templateName") templateId: String): Call<String>

    companion object {
        const val BASE_URL = "https://memeow-dev.herokuapp.com/"
    }
}
