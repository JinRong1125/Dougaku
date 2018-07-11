package shinei.com.dougaku.room

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey
import android.arch.persistence.room.TypeConverters
import shinei.com.dougaku.helper.PlayMode
import shinei.com.dougaku.model.Song

@Entity(tableName = "settings")
@TypeConverters(PlayModeConverter::class, SongsConverter::class)
data class Settings(@PrimaryKey
                    @ColumnInfo(name = "id") val id: Int,
                    @ColumnInfo(name = "playModel") val playMode: PlayMode,
                    @ColumnInfo(name = "songs") val songsList: List<Song>,
                    @ColumnInfo(name = "track") val track: Int)