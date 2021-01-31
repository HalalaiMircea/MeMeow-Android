package ro.unibuc.cs.memeow.model.source

import ro.unibuc.cs.memeow.api.MemeowApi
import ro.unibuc.cs.memeow.model.PostedMeme

class MemeHistoryPagingSource(
    private val memeowApi: MemeowApi,
    userUuid: String
) : SimplePagingSource<PostedMeme>(userUuid) {

    override suspend fun callRetrofitApi(identifier: String, position: Int, loadSize: Int) =
        memeowApi.getUserMemeHistory(identifier, position, loadSize)

}

class MemesByTemplatePagingSource(
    private val memeowApi: MemeowApi,
    templateName: String
) : SimplePagingSource<PostedMeme>(templateName) {

    override suspend fun callRetrofitApi(identifier: String, position: Int, loadSize: Int) =
        memeowApi.getMemesByTemplate(identifier, position, loadSize)

}
