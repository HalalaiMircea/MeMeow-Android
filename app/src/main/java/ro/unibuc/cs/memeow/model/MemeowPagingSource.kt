package ro.unibuc.cs.memeow.model

import androidx.paging.PagingSource
import retrofit2.HttpException
import ro.unibuc.cs.memeow.api.MemeowApi
import java.io.IOException

private const val MEMEOW_STARTING_PAGE_INDEX = 0

class MemeowPagingSource(
    private val memeowApi: MemeowApi,
    private val searchQuery: String?
) : PagingSource<Int, MemeTemplate>() {

    private var availablePages = 0

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, MemeTemplate> {
        val position = params.key ?: MEMEOW_STARTING_PAGE_INDEX

        return try {
            val templateList: List<MemeTemplate>
//            if (availablePages == 0) {
            templateList = memeowApi.getAvailableTemplates(searchQuery, null, position, params.loadSize)
//            }
            //memeowApi.getUnavailableTemplates(searchQuery, position, params.loadSize)


            // If we reached the end of available memes, unavailable memes will follow in next pages
            // This var is 0 only when didn't load any unavailable memes
            // list is empty and var is 0 only when we reached the end of available memes
            /*if (templateList.isEmpty() && availablePages == 0) {
                availablePages = position
            }*/

            LoadResult.Page(
                data = templateList,
                prevKey = if (position == MEMEOW_STARTING_PAGE_INDEX) null else position - 1,
                nextKey = if (templateList.isEmpty()) null else position + 1
            )
        } catch (ex: IOException) {
            // No internet connection
            LoadResult.Error(ex)
        } catch (ex: HttpException) {
            // Crap data
            LoadResult.Error(ex)
        }
    }
}