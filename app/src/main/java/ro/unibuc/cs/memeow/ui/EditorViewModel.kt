package ro.unibuc.cs.memeow.ui

import android.graphics.Bitmap
import android.util.Log
import androidx.lifecycle.*
import androidx.paging.PagingData
import androidx.paging.cachedIn
import dagger.hilt.android.lifecycle.HiltViewModel
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import ro.unibuc.cs.memeow.api.MemeowApi
import ro.unibuc.cs.memeow.model.MemeTemplate
import ro.unibuc.cs.memeow.model.PostedMeme
import ro.unibuc.cs.memeow.model.TemplateRepository
import java.io.ByteArrayOutputStream
import javax.inject.Inject

@HiltViewModel
class EditorViewModel @Inject constructor(
    repository: TemplateRepository,
    private val memeowApi: MemeowApi
) : ViewModel() {

    private val currentQuery = MutableLiveData<String?>(null)

    val templates: LiveData<PagingData<MemeTemplate>> =
        currentQuery.switchMap { queryString ->
            repository.getTemplateResults(queryString).cachedIn(viewModelScope)
        }

    var newMemeLink = MutableLiveData<PostedMeme>()
    lateinit var currentTemplate: MemeTemplate

    fun searchTemplate(query: String) {
        currentQuery.value = query
    }

    fun uploadMemeImage(bitmap: Bitmap) {
        val stream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)

        val image = MultipartBody.Part.createFormData(
            "file", "", RequestBody.create(MediaType.parse("image/png"), stream.toByteArray())
        )
        val templateId = currentTemplate.templateName
        memeowApi.uploadMeme(image, templateId).enqueue(object : Callback<PostedMeme> {
            override fun onResponse(call: Call<PostedMeme>, response: Response<PostedMeme>) {
                if (response.isSuccessful)
                    newMemeLink.value = response.body()
                else
                    Log.e(TAG, response.message() + response.code())
            }

            override fun onFailure(call: Call<PostedMeme>, t: Throwable) {
                //newMemeLink.value = t.toString()
                Log.e(TAG, t.toString())
            }
        })
    }

    companion object {
        private const val TAG = "EditorViewModel"
    }
}