package ro.unibuc.cs.memeow.model.repo

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import ro.unibuc.cs.memeow.api.MemeowApi
import ro.unibuc.cs.memeow.model.LeaderboardDTO
import ro.unibuc.cs.memeow.model.Ranking
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RankingRepository @Inject constructor(private val memeowApi: MemeowApi) {

    fun getRankingList(): LiveData<List<Ranking>> {
        val result = MutableLiveData<List<Ranking>>()

        memeowApi.getAllRankings().enqueue(object : Callback<LeaderboardDTO> {
            override fun onResponse(call: Call<LeaderboardDTO>, response: Response<LeaderboardDTO>) {
                if (response.isSuccessful)
                    result.value = response.body()!!.entries
                else
                    Log.d(TAG, "onResponse: ${response.message() + response.code()}")
            }

            override fun onFailure(call: Call<LeaderboardDTO>, t: Throwable) {
                Log.e(TAG, "onFailure: $t")
            }
        })
        return result
    }

    fun getTopRanking(): LiveData<Ranking> {
        val result = MutableLiveData<Ranking>()

        memeowApi.getRanking(1).enqueue(object : Callback<Ranking> {
            override fun onResponse(call: Call<Ranking>, response: Response<Ranking>) {
                if (response.isSuccessful)
                    result.value = response.body()
                else
                    Log.d(TAG, "onResponse: ${response.message() + response.code()}")
            }

            override fun onFailure(call: Call<Ranking>, t: Throwable) {
                Log.e(TAG, "onFailure: $t")
            }
        })
        return result
    }

    companion object {
        private const val TAG = "RankingRepository"
    }
}