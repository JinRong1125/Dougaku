/*
 * Copyright (C) 2017 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package shinei.com.dougaku.di

import android.app.Application
import android.arch.persistence.room.Room
import dagger.Module
import dagger.Provides
import shinei.com.dougaku.api.DougakuRepository
import shinei.com.dougaku.room.*
import javax.inject.Singleton

@Module(includes = [ViewModelModule::class])
class AppModule {
    @Singleton
    @Provides
    fun provideDougakuRepository(): DougakuRepository {
        return DougakuRepository()
    }

    @Singleton
    @Provides
    fun provideDougakuDatabase(application: Application): DougakuDatabase {
        return Room
                .databaseBuilder(application, DougakuDatabase::class.java, "Dougaku.db")
                .fallbackToDestructiveMigration()
                .build()
    }

    @Singleton
    @Provides
    fun provideAlbumSongsDao(dougakuDatabase: DougakuDatabase): AlbumSongsDao {
        return dougakuDatabase.albumSongsDao()
    }

    @Singleton
    @Provides
    fun provideLikedTracksDao(dougakuDatabase: DougakuDatabase): LikedTracksDao {
        return dougakuDatabase.likedTracksDao()
    }

    @Singleton
    @Provides
    fun provideLikedAlbumsDao(dougakuDatabase: DougakuDatabase): LikedAlbumsDao {
        return dougakuDatabase.likedAlbumsDao()
    }

    @Singleton
    @Provides
    fun provideMyPlaylistsDao(dougakuDatabase: DougakuDatabase): MyPlaylistsDao {
        return dougakuDatabase.myPlaylistsDao()
    }

    @Singleton
    @Provides
    fun provideHistoryTracksDao(dougakuDatabase: DougakuDatabase): HistoryTracksDao {
        return dougakuDatabase.historyTracksDao()
    }

    @Singleton
    @Provides
    fun provideSettingsDao(dougakuDatabase: DougakuDatabase): SettingsDao {
        return dougakuDatabase.settingsDao()
    }

    @Singleton
    @Provides
    fun provideSearchHistoriesDao(dougakuDatabase: DougakuDatabase): SearchHistoriesDao {
        return dougakuDatabase.searchHistoriesDao()
    }
}
