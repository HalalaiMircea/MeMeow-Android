package ro.unibuc.cs.memeow.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

data class FacebookAuthUser(
    val accessToken: String,
    val userId: String
)

data class MemeTemplate(
    val templateName: String,
    val minRequiredLevel: Int,
    val imageUrl: String
)

data class ServerAuthResponse(
    val jwtToken: String
)

@Parcelize
data class PostedMemeResponse(
    val memeBusinessId: String,
    val memeUrl: String,
    val reactionCount: Int,
    val dateTimeUtc: String
) : Parcelable