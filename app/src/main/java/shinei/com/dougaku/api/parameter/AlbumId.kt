package shinei.com.dougaku.api.parameter

import com.google.gson.annotations.SerializedName

data class AlbumId(
        @SerializedName("id_album") val albumId: Int): RequestCode()