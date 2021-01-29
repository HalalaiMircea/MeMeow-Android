package ro.unibuc.cs.memeow.ui

import android.content.SharedPreferences
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

const val API_KEY = "apiKey"

@HiltViewModel
class UserViewModel @Inject constructor(
    //private val repository: AuthRepository,
    private val sharedPrefs: SharedPreferences
) : ViewModel() {

    // Contains True if api key exists in shared prefs
    private val _loggedInState: MutableLiveData<Boolean> =
        MutableLiveData(sharedPrefs.getString(API_KEY, null) != null)

    val loggedInState: LiveData<Boolean> get() = _loggedInState

    fun saveJwtToken(token: String) {
        sharedPrefs.edit()
            .putString(API_KEY, token)
            .apply()

        _loggedInState.value = true
    }

    fun removeJwtToken() {
        sharedPrefs.edit()
            .remove(API_KEY)
            .apply()

        _loggedInState.value = false
    }
}
