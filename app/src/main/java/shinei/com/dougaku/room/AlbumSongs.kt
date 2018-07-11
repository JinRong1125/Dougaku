package shinei.com.dougaku.room

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey
import android.arch.persistence.room.TypeConverters
import shinei.com.dougaku.model.Song

@Entity(tableName = "albumSongs")
@TypeConverters(SongsConverter::class)
data class AlbumSongs(@PrimaryKey
                      @ColumnInfo(name = "albumId") val albumId: Int,
                      @ColumnInfo(name = "songs") val songsList: List<Song>)