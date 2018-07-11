package shinei.com.dougaku.room

import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert
import android.arch.persistence.room.OnConflictStrategy
import android.arch.persistence.room.Query
import io.reactivex.Single

@Dao
interface AlbumSongsDao {

    @Query("SELECT * FROM albumSongs WHERE albumId = :albumId")
    fun getAlbumSongs(albumId: Int): Single<AlbumSongs>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAlbumSongs(albumSongs: AlbumSongs)
}