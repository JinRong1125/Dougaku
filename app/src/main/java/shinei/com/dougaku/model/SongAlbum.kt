package shinei.com.dougaku.model

import com.google.gson.annotations.SerializedName

data class SongAlbum(
        @SerializedName("songs") val songsList: List<Song>,
        @SerializedName("albums") val albumsList: List<Album>)