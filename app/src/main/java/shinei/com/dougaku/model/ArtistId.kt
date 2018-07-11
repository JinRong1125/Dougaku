package shinei.com.dougaku.model

import com.google.gson.annotations.SerializedName

data class ArtistId(
        @SerializedName("id_artist") val artistId: Int): RequestCode()