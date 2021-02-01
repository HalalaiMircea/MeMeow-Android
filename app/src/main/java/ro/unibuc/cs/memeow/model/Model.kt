package ro.unibuc.cs.memeow.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.util.*

data class FacebookAuthUser(
    val accessToken: String,
    val userId: String
)

data class MemeTemplate(
    val templateName: String,
    val minRequiredLevel: Int,
    val imageUrl: String
)

data class ServerAuthResponse(val jwtToken: String)

@Parcelize
data class PostedMeme(
    val memeBusinessId: String,
    val memeUrl: String,
    val reactionCount: Int,
    val dateTimeUtc: Date,
    val liked: Boolean
) : Parcelable

data class Profile(
    val username: String?,
    val email: String,
    val firstName: String,
    val lastName: String,
    val iconUrl: String?,
    val userRole: String,
    val level: Level,
    val profileUuid: String,
    val lastMeme: PostedMeme?
) {
    data class Level(
        val currentXp: Int,
        val currentLevel: Int,
        val lastCurrentXp: Int,
        val lastCurrentLevel: Int
    )
}

data class Ranking(
    val leaderboardPlace: Int,
    val userResponse: User
) {
    data class User(
        val firstName: String,
        val lastName: String,
        val iconUrl: String,
        val profileUuid: String
    )
}

data class LeaderboardDTO(val entries: List<Ranking>)