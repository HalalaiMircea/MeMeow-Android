package ro.unibuc.cs.memeow.model.source

import androidx.paging.PagingSource
import retrofit2.HttpException
import java.io.IOException

abstract class SimplePagingSource<Value : Any>(
    private val identifier: String,
) : PagingSource<Int, Value>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Value> {
        val position = params.key ?: MEMEOW_STARTING_PAGE_INDEX

        return try {
            val apiCallList = callRetrofitApi(identifier, position, params.loadSize)

            LoadResult.Page(
                data = apiCallList,
                prevKey = if (position == MEMEOW_STARTING_PAGE_INDEX) null else position - 1,
                nextKey = if (apiCallList.isEmpty()) null else position + 1
            )
        } catch (ex: IOException) {
            // No internet connection
            LoadResult.Error(ex)
        } catch (ex: HttpException) {
            // Crap data
            LoadResult.Error(ex)
        }
    }

    protected abstract suspend fun callRetrofitApi(
        identifier: String,
        position: Int,
        loadSize: Int
    ): List<Value>
}

internal const val MEMEOW_STARTING_PAGE_INDEX = 0
