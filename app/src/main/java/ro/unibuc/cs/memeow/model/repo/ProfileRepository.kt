package ro.unibuc.cs.memeow.model.repo

import android.content.SharedPreferences
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import ro.unibuc.cs.memeow.api.MemeowApi
import ro.unibuc.cs.memeow.model.Profile
import javax.inject.Inject
import javax.inject.Singleton

const val JTW_TOKEN = "jwt_token"

@Singleton
class ProfileRepository @Inject constructor(
    private val memeowApi: MemeowApi,
    private val sharedPrefs: SharedPreferences
) {

    private val _signedUserProfile = MutableLiveData<Profile>()
    val signedUserProfile: LiveData<Profile> get() = _signedUserProfile

    val isUserLoggedIn: Boolean
        get() = signedUserProfile.value != null

    init {
        if (sharedPrefs.getString(JTW_TOKEN, null) != null)
            getUserProfile(null)
        else
            _signedUserProfile.value = null
    }

    fun getUserProfile(uuid: String?): LiveData<Profile> {
        val callback: ProfileCallback
        // If uuid is null, we get logged in user's profile
        if (uuid == null) {
            callback = ProfileCallback(_signedUserProfile)
            memeowApi.getOwnProfile().enqueue(callback)
        } else {
            callback = ProfileCallback(MutableLiveData<Profile>())
            memeowApi.getUserProfile(uuid).enqueue(callback)
        }
        return callback.result
    }

    /**
     * Saves the jwt token to storage and loads users's profile from backend
     */
    fun signInUser(token: String) {
        sharedPrefs.edit()
            .putString(JTW_TOKEN, token)
            .apply()
        getUserProfile(null)
    }

    /**
     * Deletes the jwt token from storage and sets the logged user to null
     */
    fun signOutUser() {
        sharedPrefs.edit()
            .remove(JTW_TOKEN)
            .apply()
        _signedUserProfile.value = null
    }

    private class ProfileCallback(val result: MutableLiveData<Profile>) : Callback<Profile> {
        override fun onResponse(call: Call<Profile>, response: Response<Profile>) {
            if (response.isSuccessful)
                result.value = response.body()
            else
                Log.e(TAG, "onResponse: ${response.message() + response.code()}")
        }

        override fun onFailure(call: Call<Profile>, t: Throwable) {
            Log.e(TAG, "onFailure: $t")
        }
    }

    companion object {
        private const val TAG = "ProfileRepository"
    }
}