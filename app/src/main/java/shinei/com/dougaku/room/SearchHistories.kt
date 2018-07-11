package shinei.com.dougaku.room

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey

@Entity(tableName = "searchHistories")
data class SearchHistories(@PrimaryKey(autoGenerate = true)
                           @ColumnInfo(name = "id") val id: Int,
                           @ColumnInfo(name = "keyword") val keyword: String)