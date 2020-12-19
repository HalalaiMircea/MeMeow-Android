package ro.unibuc.cs.memeow.model

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import ro.unibuc.cs.memeow.api.MemeowApi
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ProfileRepository @Inject constructor(private val memeowApi: MemeowApi) {

    fun getOwnProfile(): LiveData<Profile> {
        val resultLiveData = MutableLiveData<Profile>()

        memeowApi.getOwnProfile().enqueue(object : Callback<Profile> {
            override fun onResponse(call: Call<Profile>, response: Response<Profile>) {
                if (response.isSuccessful)
                    resultLiveData.value = response.body()
                else
                    Log.e(TAG, "onResponse: ${response.message() + response.code()}")
            }

            override fun onFailure(call: Call<Profile>, t: Throwable) {
                Log.e(TAG, "onFailure: $t")
            }
        })
        return resultLiveData
    }

    fun getUserProfile(uuid: String): LiveData<Profile> {
        val resultLiveData = MutableLiveData<Profile>()

        memeowApi.getUserProfile(uuid).enqueue(object : Callback<Profile> {
            override fun onResponse(call: Call<Profile>, response: Response<Profile>) {
                if (response.isSuccessful)
                    resultLiveData.value = response.body()
                else
                    Log.e(TAG, "onResponse: ${response.message() + response.code()}")
            }

            override fun onFailure(call: Call<Profile>, t: Throwable) {
                Log.e(TAG, "onFailure: $t")
            }
        })
        return resultLiveData
    }

    companion object {
        private const val TAG = "ProfileRepository"
    }
}