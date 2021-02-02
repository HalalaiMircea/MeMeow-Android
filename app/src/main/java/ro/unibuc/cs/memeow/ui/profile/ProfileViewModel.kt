package ro.unibuc.cs.memeow.ui.profile

import androidx.lifecycle.LiveData
import androidx.lifecycle.SavedStateHandle
import dagger.hilt.android.lifecycle.HiltViewModel
import ro.unibuc.cs.memeow.model.Profile
import ro.unibuc.cs.memeow.model.repo.ProfileRepository
import ro.unibuc.cs.memeow.util.ArgsViewModel
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    val repository: ProfileRepository,
    savedStateHandle: SavedStateHandle
) : ArgsViewModel(savedStateHandle) {

    private val args: ProfileFragmentArgs by navArgs()

    val profile: LiveData<Profile> = repository.getUserProfile(args.profileUUID)

    companion object {
        private const val TAG = "ProfileViewModel"
    }
}
