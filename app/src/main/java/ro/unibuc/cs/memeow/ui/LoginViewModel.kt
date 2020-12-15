package ro.unibuc.cs.memeow.ui

import android.content.SharedPreferences
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.ViewModel

const val API_KEY = "apiKey"

class LoginViewModel @ViewModelInject constructor(
    //private val repository: AuthRepository,
    private val sharedPrefs: SharedPreferences
) : ViewModel() {

    fun saveJwtToken(token: String) {
        sharedPrefs.edit()
            .putString(API_KEY, token)
            .apply()
    }
}
