package ro.unibuc.cs.memeow.ui.editor

import android.graphics.Bitmap
import android.util.Log
import androidx.lifecycle.*
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
import ro.unibuc.cs.memeow.model.repo.ProfileRepository
import ro.unibuc.cs.memeow.model.repo.TemplateRepository
import java.io.ByteArrayOutputStream
import javax.inject.Inject

@HiltViewModel
class EditorViewModel @Inject constructor(
    repository: TemplateRepository,
    val userRepository: ProfileRepository,
    private val memeowApi: MemeowApi
) : ViewModel() {

    private val currentQuery = MutableLiveData(DEFAULT_QUERY)

    val templates = currentQuery.switchMap { repository.getTemplateResults(it).cachedIn(viewModelScope) }

    lateinit var currentTemplate: MemeTemplate

    val userCurrentLevel: Int
        get() = userRepository.signedUserProfile.value?.level?.currentLevel ?: 1

    fun searchTemplate(query: String) {
        currentQuery.value = query
    }

    fun uploadMemeImage(bitmap: Bitmap): LiveData<PostedMeme> {
        val stream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)

        val image = MultipartBody.Part.createFormData(
            "file", "", RequestBody.create(MediaType.parse("image/png"), stream.toByteArray())
        )
        val templateId = currentTemplate.templateName
        val result = MutableLiveData<PostedMeme>()
        memeowApi.uploadMeme(image, templateId).enqueue(object : Callback<PostedMeme> {
            override fun onResponse(call: Call<PostedMeme>, response: Response<PostedMeme>) {
                if (response.isSuccessful)
                    result.value = response.body()
                else
                    Log.e(TAG, response.message() + response.code())
            }

            override fun onFailure(call: Call<PostedMeme>, t: Throwable) {
                Log.e(TAG, "onFailure: $t")
            }
        })
        return result
    }

    companion object {
        private const val DEFAULT_QUERY = ""
        private const val TAG = "EditorViewModel"
    }
}