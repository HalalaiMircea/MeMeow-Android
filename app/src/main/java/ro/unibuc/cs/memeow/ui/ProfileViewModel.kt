package ro.unibuc.cs.memeow.ui

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import ro.unibuc.cs.memeow.model.Profile
import ro.unibuc.cs.memeow.model.ProfileRepository

class ProfileViewModel @ViewModelInject constructor(
    private val repository: ProfileRepository,
) : ViewModel() {

    fun getProfile(uuid: String?): LiveData<Profile> {
        return if (uuid != null)
            repository.getUserProfile(uuid)
        else
            repository.getOwnProfile()
    }

    companion object {
        private const val TAG = "ProfileViewModel"
    }
}
