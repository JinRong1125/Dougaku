package shinei.com.dougaku.model

import android.os.Parcel
import android.os.Parcelable
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
        @SerializedName("genre") val genre: String): Parcelable {

    constructor(parcel: Parcel) : this(
            parcel.readInt(),
            parcel.readInt(),
            parcel.readInt(),
            parcel.readInt(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString())

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeInt(songId)
        dest.writeInt(disc)
        dest.writeInt(track)
        dest.writeInt(albumId)
        dest.writeString(title)
        dest.writeString(streamUrl)
        dest.writeString(album)
        dest.writeString(coverUrl)
        dest.writeString(artist)
        dest.writeString(genre)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Song> {
        override fun createFromParcel(parcel: Parcel): Song {
            return Song(parcel)
        }

        override fun newArray(size: Int): Array<Song?> {
            return arrayOfNulls(size)
        }
    }
}
