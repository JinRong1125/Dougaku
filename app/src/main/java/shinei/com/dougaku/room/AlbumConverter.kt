package shinei.com.dougaku.room

import android.arch.persistence.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import shinei.com.dougaku.model.Album

class AlbumConverter {

    val type = object : TypeToken<Album>(){}.type

    @TypeConverter
    fun albumToJson(album: Album): String {
        return Gson().toJson(album, type)
    }

    @TypeConverter
    fun jsonToAlbum(json: String): Album {
        return Gson().fromJson(json, type)
    }
}

