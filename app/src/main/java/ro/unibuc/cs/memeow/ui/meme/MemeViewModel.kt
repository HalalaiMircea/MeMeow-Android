package ro.unibuc.cs.memeow.ui.meme

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import dagger.hilt.android.lifecycle.HiltViewModel
import ro.unibuc.cs.memeow.model.PostedMeme
import ro.unibuc.cs.memeow.model.repo.MemeRepository
import ro.unibuc.cs.memeow.util.ArgsViewModel
import javax.inject.Inject

@HiltViewModel
class MemeViewModel @Inject constructor(
    private val repository: MemeRepository,
    savedStateHandle: SavedStateHandle
) : ArgsViewModel(savedStateHandle) {

    private val args: MemeFragmentArgs by navArgs()

    private val _postedMeme = MutableLiveData(args.memeObject)

    val postedMeme: LiveData<PostedMeme> get() = _postedMeme

    fun likeMeme() {
        repository.likeAndUpdateMeme(args.memeObject.memeBusinessId, _postedMeme)
    }

    companion object {
        private const val TAG = "MemeViewModel"
    }
}