package shinei.com.dougaku.room

import android.arch.persistence.room.*
import io.reactivex.Single

@Dao
interface LikedAlbumsDao {

    @Query("SELECT * FROM likedAlbums")
    fun getLikedAlbums(): Single<List<LikedAlbums>>

    @Query("SELECT * FROM likedAlbums WHERE albumId = :albumId")
    fun getLikedAlbum(albumId: Int): Single<LikedAlbums>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertLikedAlbums(likedAlbums: LikedAlbums)

    @Delete()
    fun deleteLikedAlbums(likedAlbums: LikedAlbums)
}