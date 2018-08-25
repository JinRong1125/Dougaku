package shinei.com.dougaku.api.parameter

import com.google.gson.annotations.SerializedName

data class ArtistId(
        @SerializedName("id_artist") val artistId: Int): RequestCode()