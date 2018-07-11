package shinei.com.dougaku.room

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey
import android.arch.persistence.room.TypeConverters
import shinei.com.dougaku.model.Album

@Entity(tableName = "likedAlbums")
@TypeConverters(AlbumConverter::class)
data class LikedAlbums(@PrimaryKey(autoGenerate = true)
                       @ColumnInfo(name = "id") val id: Int,
                       @ColumnInfo(name = "albumId") val albumId: Int,
                       @ColumnInfo(name = "album") val album: Album)