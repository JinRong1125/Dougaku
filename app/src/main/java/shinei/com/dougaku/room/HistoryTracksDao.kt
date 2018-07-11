package shinei.com.dougaku.room

import android.arch.persistence.room.*
import io.reactivex.Single

@Dao
interface HistoryTracksDao {

    @Query("SELECT * FROM historyTracks")
    fun getHistoryTracks(): Single<List<HistoryTracks>>

    @Query("SELECT * FROM historyTracks WHERE songId = :songId")
    fun getHistoryTrack(songId: Int): Single<HistoryTracks>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertHistoryTracks(historyTracks: HistoryTracks)

    @Query("DELETE FROM historyTracks")
    fun deleteHistoryTracks()

    @Delete()
    fun deleteHistoryTracks(historyTracks: HistoryTracks)
}