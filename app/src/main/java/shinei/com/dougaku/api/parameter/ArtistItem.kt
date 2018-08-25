package shinei.com.dougaku.api.parameter

import com.google.gson.annotations.SerializedName

data class ArtistItem(
        @SerializedName("start_point") val startPoint: Long,
        @SerializedName("counts") val counts: Int): RequestCode()