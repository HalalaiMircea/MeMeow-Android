package ro.unibuc.cs.memeow.ui

import androidx.hilt.Assisted
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.SavedStateHandle
import ro.unibuc.cs.memeow.model.Profile
import ro.unibuc.cs.memeow.model.ProfileRepository
import ro.unibuc.cs.memeow.util.ArgsViewModel

class ProfileViewModel @ViewModelInject constructor(
    repository: ProfileRepository,
    @Assisted savedStateHandle: SavedStateHandle
) : ArgsViewModel(savedStateHandle) {

    private val args: ProfileFragmentArgs by navArgs()

    val profile: LiveData<Profile> = if (args.profileUUID != null) {
        repository.getUserProfile(args.profileUUID!!)
    } else {
        repository.getOwnProfile()
    }

    companion object {
        private const val TAG = "ProfileViewModel"
    }
}
