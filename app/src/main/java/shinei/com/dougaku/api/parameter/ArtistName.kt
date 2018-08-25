package shinei.com.dougaku.api.parameter

import com.google.gson.annotations.SerializedName

data class ArtistName(
        @SerializedName("name_artist") val artistName: String): RequestCode()