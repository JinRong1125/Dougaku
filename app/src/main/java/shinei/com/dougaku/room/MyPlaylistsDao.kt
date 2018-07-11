package shinei.com.dougaku.room

import android.arch.persistence.room.*
import io.reactivex.Single

@Dao
interface MyPlaylistsDao {

    @Query("SELECT * FROM myPlaylists")
    fun getMyPlaylists(): Single<List<MyPlaylists>>

    @Query("SELECT * FROM myPlaylists WHERE title = :title")
    fun getMyPlaylist(title: String): Single<MyPlaylists>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertMyPlaylists(myPlaylists: MyPlaylists)

    @Delete()
    fun deleteMyPlaylists(myPlaylists: MyPlaylists)

    @Update()
    fun updateMyPlaylists(myPlaylists: MyPlaylists)
}