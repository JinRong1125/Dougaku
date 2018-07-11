package shinei.com.dougaku.room

import android.arch.persistence.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import shinei.com.dougaku.model.Song

class SongConverter {

    val type = object : TypeToken<Song>(){}.type

    @TypeConverter
    fun songToJson(song: Song): String {
        return Gson().toJson(song, type)
    }

    @TypeConverter
    fun jsonToSong(json: String): Song {
        return Gson().fromJson(json, type)
    }
}

