package ro.unibuc.cs.memeow.api

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import ro.unibuc.cs.memeow.model.FacebookAuthUser
import ro.unibuc.cs.memeow.model.MemeTemplate
import ro.unibuc.cs.memeow.model.ServerAuthResponse

interface MemeowApi {

    @GET("albums/{albumId}/photos")
    suspend fun getPhotosForAlbum(@Path("albumId") albumId: Int): List<MemeTemplate>

    @POST("api/authenticate/facebook")
    fun facebookAuth(@Body body:FacebookAuthUser): Call<ServerAuthResponse>


    companion object {
        const val BASE_URL = "https://memeow-dev.herokuapp.com/"

    }
}