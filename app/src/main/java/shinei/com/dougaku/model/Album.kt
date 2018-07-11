package shinei.com.dougaku.model

import com.google.gson.annotations.SerializedName

data class Album(
        @SerializedName("id_album") val albumId: Int,
        @SerializedName("title") val title: String,
        @SerializedName("url_cover") val coverUrl: String,
        @SerializedName("name_producer") val producer: String,
        @SerializedName("event") val event: String)