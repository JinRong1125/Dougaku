package shinei.com.dougaku.room

import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert
import android.arch.persistence.room.OnConflictStrategy
import android.arch.persistence.room.Query
import io.reactivex.Single

@Dao
interface SettingsDao {

    @Query("SELECT * FROM settings WHERE id = :id")
    fun getSettings(id: Int): Single<Settings>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertSettings(settings: Settings)
}