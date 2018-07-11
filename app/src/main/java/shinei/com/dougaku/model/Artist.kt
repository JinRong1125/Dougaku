package shinei.com.dougaku.model

import com.google.gson.annotations.SerializedName

data class Artist(
        @SerializedName("id_artist") val artistId: Int,
        @SerializedName("name") val name: String,
        @SerializedName("url_image") val imageUrl: String,
        @SerializedName("counts_song") val songCounts: Int)