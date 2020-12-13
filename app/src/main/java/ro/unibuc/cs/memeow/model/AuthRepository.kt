package ro.unibuc.cs.memeow.model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import ro.unibuc.cs.memeow.api.MemeowApi
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepository @Inject constructor(private val memeowApi: MemeowApi){
    fun authWithFacebook(facebookAuthUser: FacebookAuthUser): LiveData<String> {
        val result = MutableLiveData<String>()
        Thread(){
            //result.postValue(memeowApi.facebookAuth(facebookAuthUser))
        }
        return result
    }
}