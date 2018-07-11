package shinei.com.dougaku.room

import android.arch.persistence.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import shinei.com.dougaku.helper.PlayMode

class PlayModeConverter {

    val type = object : TypeToken<PlayMode>(){}.type

    @TypeConverter
    fun playModeToJson(playMode: PlayMode): String {
        return Gson().toJson(playMode, type)
    }

    @TypeConverter
    fun jsonToPlayMode(json: String): PlayMode {
        return Gson().fromJson(json, type)
    }
}

