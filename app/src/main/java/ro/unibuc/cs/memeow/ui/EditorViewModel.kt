package ro.unibuc.cs.memeow.ui

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.paging.PagingData
import ro.unibuc.cs.memeow.model.MemeTemplate
import ro.unibuc.cs.memeow.model.TemplateRepository

class EditorViewModel @ViewModelInject constructor(
    private val repository: TemplateRepository
) : ViewModel() {

//    private val currew

    val templates: LiveData<PagingData<MemeTemplate>> = repository.getTemplatePage()
}