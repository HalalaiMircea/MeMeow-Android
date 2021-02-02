package ro.unibuc.cs.memeow.ui.meme

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import dagger.hilt.android.lifecycle.HiltViewModel
import ro.unibuc.cs.memeow.model.repo.MemeRepository
import ro.unibuc.cs.memeow.util.ArgsViewModel
import javax.inject.Inject

@HiltViewModel
class MemeHistoryViewModel @Inject constructor(
    repository: MemeRepository,
    savedStateHandle: SavedStateHandle
) : ArgsViewModel(savedStateHandle) {

    private val args: MemeHistoryFragmentArgs by navArgs()

    val memePageData = repository.getUserMemeHistory(args.profileUUID).cachedIn(viewModelScope)

}