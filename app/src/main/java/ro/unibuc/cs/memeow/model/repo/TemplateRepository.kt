package ro.unibuc.cs.memeow.model.repo

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.liveData
import ro.unibuc.cs.memeow.api.MemeowApi
import ro.unibuc.cs.memeow.model.source.TemplatePagingSource
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TemplateRepository @Inject constructor(private val memeowApi: MemeowApi) {

    // maxSize >= pageSize + 2*prefetchDist
    fun getTemplateResults(search: String?) =
        Pager(
            config = PagingConfig(
                pageSize = 20,
                maxSize = 60,
                enablePlaceholders = false
            ),
            pagingSourceFactory = { TemplatePagingSource(memeowApi, search) }
        ).liveData
}