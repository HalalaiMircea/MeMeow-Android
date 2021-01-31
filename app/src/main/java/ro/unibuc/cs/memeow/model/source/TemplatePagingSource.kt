package ro.unibuc.cs.memeow.model.source

import androidx.paging.PagingSource
import retrofit2.HttpException
import ro.unibuc.cs.memeow.api.MemeowApi
import ro.unibuc.cs.memeow.model.MemeTemplate
import java.io.IOException

class TemplatePagingSource(
    private val memeowApi: MemeowApi,
    private val searchQuery: String?
) : PagingSource<Int, MemeTemplate>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, MemeTemplate> {
        val position = params.key ?: MEMEOW_STARTING_PAGE_INDEX

        return try {
            val templateList = memeowApi.getAvailableTemplates(searchQuery, null, position, params.loadSize)

            if (templateList.isEmpty()) {
                val lastList = memeowApi.getUnavailableTemplates(searchQuery, 0, params.loadSize)
                LoadResult.Page(
                    data = lastList,
                    prevKey = if (position == MEMEOW_STARTING_PAGE_INDEX) null else position - 1,
                    nextKey = null
                )
            } else {
                LoadResult.Page(
                    data = templateList,
                    prevKey = if (position == MEMEOW_STARTING_PAGE_INDEX) null else position - 1,
                    nextKey = if (templateList.isEmpty()) null else position + 1
                )
            }
        } catch (ex: IOException) {
            // No internet connection
            LoadResult.Error(ex)
        } catch (ex: HttpException) {
            // Crap data
            LoadResult.Error(ex)
        }
    }
}
