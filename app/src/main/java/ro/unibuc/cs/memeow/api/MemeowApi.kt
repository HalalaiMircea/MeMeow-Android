package ro.unibuc.cs.memeow.api

import retrofit2.http.GET
import retrofit2.http.Path
import ro.unibuc.cs.memeow.model.MemeTemplate

interface MemeowApi {

    @GET("albums/{albumId}/photos")
    suspend fun getPhotosForAlbum(@Path("albumId") albumId: Int): List<MemeTemplate>

    companion object {
        const val BASE_URL = "https://jsonplaceholder.typicode.com/"

    }
}