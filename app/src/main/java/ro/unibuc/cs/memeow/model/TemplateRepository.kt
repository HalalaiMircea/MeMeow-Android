package ro.unibuc.cs.memeow.model

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.liveData
import ro.unibuc.cs.memeow.api.MemeowApi
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TemplateRepository @Inject constructor(private val memeowApi: MemeowApi) {
    fun getTemplatePage() =
        Pager(
            config = PagingConfig(
                pageSize = 50/*FIXED SIZE BY JsonPlaceholder*/,
                maxSize = 150,
                enablePlaceholders = false
            ),
            pagingSourceFactory = { MemeowPagingSource(memeowApi) }
        ).liveData
}