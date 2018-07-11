package shinei.com.dougaku.room

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey
import android.arch.persistence.room.TypeConverters
import shinei.com.dougaku.model.Song

@Entity(tableName = "historyTracks")
@TypeConverters(SongConverter::class)
data class HistoryTracks(@PrimaryKey(autoGenerate = true)
                         @ColumnInfo(name = "id") val id: Int,
                         @ColumnInfo(name = "songId") val songId: Int,
                         @ColumnInfo(name = "song") val song: Song)