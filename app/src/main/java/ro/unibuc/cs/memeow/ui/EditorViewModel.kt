package ro.unibuc.cs.memeow.ui

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import ro.unibuc.cs.memeow.model.MemeTemplate
import ro.unibuc.cs.memeow.model.TemplateRepository

class EditorViewModel @ViewModelInject constructor(
    repository: TemplateRepository
) : ViewModel() {

    val templates: LiveData<PagingData<MemeTemplate>> =
        repository.getTemplatePage().cachedIn(viewModelScope)

}