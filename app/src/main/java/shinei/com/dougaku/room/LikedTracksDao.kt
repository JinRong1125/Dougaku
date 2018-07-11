package shinei.com.dougaku.room

import android.arch.persistence.room.*
import io.reactivex.Single

@Dao
interface LikedTracksDao {

    @Query("SELECT * FROM likedTracks")
    fun getLikedTracks(): Single<List<LikedTracks>>

    @Query("SELECT * FROM likedTracks WHERE songId = :songId")
    fun getLikedTrack(songId: Int): Single<LikedTracks>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertLikedTracks(likedTracks: LikedTracks)

    @Delete()
    fun deleteLikedTracks(likedTracks: LikedTracks)
}