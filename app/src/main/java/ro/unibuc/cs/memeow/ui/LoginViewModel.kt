package ro.unibuc.cs.memeow.ui

import androidx.arch.core.util.Function
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.google.gson.JsonParser
import ro.unibuc.cs.memeow.model.AuthRepository
import ro.unibuc.cs.memeow.model.FacebookAuthUser

class LoginViewModel @ViewModelInject constructor(
    private val repository: AuthRepository
): ViewModel(){
    //val jwtTokenLd = MutableLiveData<String>()
    fun getJwtToken(fbAuthUser: FacebookAuthUser): LiveData<String> {
        return Transformations.map(
            repository.authWithFacebook(fbAuthUser),
            Function<String, String> {
                val jsonParser= JsonParser()
                return@Function jsonParser.parse(it)
                    .asJsonObject.get("accessToken").asString
            })
    }
}
