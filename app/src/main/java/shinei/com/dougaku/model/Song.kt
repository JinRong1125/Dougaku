package shinei.com.dougaku.model

import com.google.gson.annotations.SerializedName

data class Song(
        @SerializedName("id_song") val songId: Int,
        @SerializedName("disc") val disc: Int,
        @SerializedName("track") val track: Int,
        @SerializedName("id_album") val albumId: Int,
        @SerializedName("title") val title: String,
        @SerializedName("url_stream") val streamUrl: String,
        @SerializedName("album") val album: String,
        @SerializedName("url_cover") val coverUrl: String,
        @SerializedName("artist") val artist: String,
        @SerializedName("genre") val genre: String,
        @SerializedName("artist_list") val artistList: List<String>)
