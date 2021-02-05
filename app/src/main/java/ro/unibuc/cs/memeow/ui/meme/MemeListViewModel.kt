package ro.unibuc.cs.memeow.ui.meme

import android.os.Bundle
import android.util.Log
import androidx.lifecycle.AbstractSavedStateViewModelFactory
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import androidx.savedstate.SavedStateRegistryOwner
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import ro.unibuc.cs.memeow.R
import ro.unibuc.cs.memeow.model.repo.MemeRepository
import ro.unibuc.cs.memeow.util.ArgsViewModel

//@HiltViewModel
class MemeListViewModel @AssistedInject constructor(
    repository: MemeRepository,
    @Assisted savedStateHandle: SavedStateHandle,
    @Assisted prevDestination: Int
) : ArgsViewModel(savedStateHandle) {

    private val args: MemeListFragmentArgs by navArgs()

    val memeData = if (prevDestination == R.id.nav_create_meme) {
        repository.getMemesByTemplate(args.identifier)
    } else {
        repository.getUserMemeHistory(args.identifier)
    }.cachedIn(viewModelScope)

    init {
        Log.e(TAG, "MemeListVM $prevDestination")
    }

    @AssistedFactory
    interface Factory {
        fun create(handle: SavedStateHandle, prevDestination: Int): MemeListViewModel
    }

    companion object {
        private const val TAG = "MemeListViewModel"

        fun provideFactory(
            assistedFactory: Factory,
            owner: SavedStateRegistryOwner,
            defaultArgs: Bundle? = null,
            prevDestination: Int
        ): AbstractSavedStateViewModelFactory =
            object : AbstractSavedStateViewModelFactory(owner, defaultArgs) {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel?> create(
                    key: String,
                    modelClass: Class<T>,
                    handle: SavedStateHandle
                ): T {
                    return assistedFactory.create(handle, prevDestination) as T
                }
            }
    }
}