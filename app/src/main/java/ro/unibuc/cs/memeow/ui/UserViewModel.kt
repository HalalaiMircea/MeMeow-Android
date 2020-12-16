package ro.unibuc.cs.memeow.ui

import android.content.SharedPreferences
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

const val API_KEY = "apiKey"

class UserViewModel @ViewModelInject constructor(
    //private val repository: AuthRepository,
    private val sharedPrefs: SharedPreferences
) : ViewModel() {

    // Contains True if api key exists in shared prefs
    val loggedInState: MutableLiveData<Boolean> =
        MutableLiveData(sharedPrefs.getString(API_KEY, null) != null)

    fun saveJwtToken(token: String) {
        sharedPrefs.edit()
            .putString(API_KEY, token)
            .apply()

        loggedInState.value = true
    }

    fun removeJwtToken() {
        sharedPrefs.edit()
            .remove(API_KEY)
            .apply()

        loggedInState.value = false
    }
}
