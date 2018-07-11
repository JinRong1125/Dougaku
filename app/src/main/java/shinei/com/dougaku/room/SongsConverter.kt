package shinei.com.dougaku.room

import android.arch.persistence.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import shinei.com.dougaku.model.Song

class SongsConverter {

    val type = object : TypeToken<List<Song>>(){}.type

    @TypeConverter
    fun songsListToJson(songsList: List<Song>): String {
        return Gson().toJson(songsList, type)
    }

    @TypeConverter
    fun jsonToSongsList(json: String): List<Song> {
        return Gson().fromJson(json, type)
    }
}

