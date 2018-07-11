package shinei.com.dougaku.room

import android.arch.persistence.room.Database
import android.arch.persistence.room.RoomDatabase

@Database(entities = [
    AlbumSongs::class,
    LikedTracks::class,
    LikedAlbums::class,
    MyPlaylists::class,
    HistoryTracks::class,
    Settings::class,
    SearchHistories::class], version = 1)
abstract class DougakuDatabase : RoomDatabase() {

    abstract fun albumSongsDao(): AlbumSongsDao

    abstract fun likedTracksDao(): LikedTracksDao

    abstract fun likedAlbumsDao(): LikedAlbumsDao

    abstract fun myPlaylistsDao(): MyPlaylistsDao

    abstract fun historyTracksDao(): HistoryTracksDao

    abstract fun settingsDao(): SettingsDao

    abstract fun searchHistoriesDao(): SearchHistoriesDao
}