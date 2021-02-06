package ro.unibuc.cs.memeow.model.repo

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.liveData
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import ro.unibuc.cs.memeow.api.MemeowApi
import ro.unibuc.cs.memeow.model.PostedMeme
import ro.unibuc.cs.memeow.model.source.MemeHistoryPagingSource
import ro.unibuc.cs.memeow.model.source.MemesByTemplatePagingSource
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MemeRepository @Inject constructor(private val memeowApi: MemeowApi) {

    fun getUserMemeHistory(uuid: String) =
        Pager(
            config = PagingConfig(
                pageSize = 10,
                maxSize = 30,
                enablePlaceholders = false
            ),
            pagingSourceFactory = { MemeHistoryPagingSource(memeowApi, uuid) }
        ).liveData

    fun getMemesByTemplate(templateName: String) =
        Pager(
            config = PagingConfig(
                pageSize = 10,
                maxSize = 30,
                enablePlaceholders = false
            ),
            pagingSourceFactory = { MemesByTemplatePagingSource(memeowApi, templateName) }
        ).liveData

    fun likeAndUpdateMeme(memeId: String, memeLiveData: MutableLiveData<PostedMeme>) {
        memeowApi.likeMeme(memeId).enqueue(object : Callback<PostedMeme> {
            override fun onResponse(call: Call<PostedMeme>, response: Response<PostedMeme>) {
                if (response.isSuccessful) {
                    memeLiveData.value = response.body()
                } else {
                    Log.d(TAG, "onResponse: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<PostedMeme>, t: Throwable) {
                Log.e(TAG, "onFailure: $t")
            }
        })
    }

    companion object {
        private const val TAG = "MemeRepository"
    }
}
