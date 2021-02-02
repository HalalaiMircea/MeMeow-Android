package ro.unibuc.cs.memeow.model.repo

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.liveData
import ro.unibuc.cs.memeow.api.MemeowApi
import ro.unibuc.cs.memeow.model.source.MemeHistoryPagingSource
import ro.unibuc.cs.memeow.model.source.MemesByTemplatePagingSource
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MemeRepository @Inject constructor(private val memeowApi: MemeowApi) {

    fun getUserMemeHistory(uuid: String) =
        Pager(
            config = PagingConfig(
                pageSize = 10,
                maxSize = 30,
                enablePlaceholders = false
            ),
            pagingSourceFactory = { MemeHistoryPagingSource(memeowApi, uuid) }
        ).liveData

    fun getMemesByTemplate(templateName: String) =
        Pager(
            config = PagingConfig(
                pageSize = 10,
                maxSize = 30,
                enablePlaceholders = false
            ),
            pagingSourceFactory = { MemesByTemplatePagingSource(memeowApi, templateName) }
        ).liveData

}
