package shinei.com.dougaku.room

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey
import android.arch.persistence.room.TypeConverters
import shinei.com.dougaku.model.Song

@Entity(tableName = "myPlaylists")
@TypeConverters(SongsConverter::class)
data class MyPlaylists(@PrimaryKey(autoGenerate = true)
                       @ColumnInfo(name = "playlistId") val playlistId: Int,
                       @ColumnInfo(name = "title") val title: String,
                       @ColumnInfo(name = "songs") val songsList: List<Song>)