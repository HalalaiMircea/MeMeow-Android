package ro.unibuc.cs.memeow.ui

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import dagger.hilt.android.lifecycle.HiltViewModel
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import ro.unibuc.cs.memeow.api.MemeowApi
import ro.unibuc.cs.memeow.model.PostedMeme
import ro.unibuc.cs.memeow.util.ArgsViewModel
import javax.inject.Inject

@HiltViewModel
class MemeViewModel @Inject constructor(
    private val memeowApi: MemeowApi,
    savedStateHandle: SavedStateHandle
) : ArgsViewModel(savedStateHandle) {

    private val args: MemeFragmentArgs by navArgs()

    private val _postedMeme = MutableLiveData(args.memeObject)

    val postedMeme: LiveData<PostedMeme> get() = _postedMeme

    fun likeMeme() {
        memeowApi.likeMeme(args.memeObject.memeBusinessId).enqueue(object : Callback<PostedMeme> {
            override fun onResponse(call: Call<PostedMeme>, response: Response<PostedMeme>) {
                if (response.isSuccessful) {
                    _postedMeme.value = response.body()
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
        private const val TAG = "MemeViewModel"
    }
}