package shinei.com.dougaku.room

import android.arch.persistence.room.*
import io.reactivex.Single

@Dao
interface SearchHistoriesDao {

    @Query("SELECT * FROM searchHistories")
    fun getSearchHistories(): Single<List<SearchHistories>>

    @Query("SELECT * FROM searchHistories WHERE keyword = :keyword")
    fun getSearchHistory(keyword: String): Single<SearchHistories>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertSearchHistories(searchHistories: SearchHistories)

    @Delete()
    fun deleteSearchHistories(searchHistories: SearchHistories)
}