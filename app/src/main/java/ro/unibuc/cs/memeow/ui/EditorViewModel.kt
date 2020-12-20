package ro.unibuc.cs.memeow.ui

import android.graphics.Bitmap
import android.util.Log
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
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

class EditorViewModel @ViewModelInject constructor(
    repository: TemplateRepository,
    private val memeowApi: MemeowApi
) : ViewModel() {

    val templates: LiveData<PagingData<MemeTemplate>> =
        repository.getTemplateResults(null).cachedIn(viewModelScope)

    lateinit var currentTemplate: MemeTemplate

    var newMemeLink = MutableLiveData<PostedMeme>()

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