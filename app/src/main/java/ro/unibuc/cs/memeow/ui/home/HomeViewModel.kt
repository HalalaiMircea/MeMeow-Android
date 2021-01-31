package ro.unibuc.cs.memeow.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import ro.unibuc.cs.memeow.model.Ranking
import ro.unibuc.cs.memeow.model.repo.RankingRepository
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(repository: RankingRepository) : ViewModel() {

    val topRanking: LiveData<Ranking> = repository.getTopRanking()

}
