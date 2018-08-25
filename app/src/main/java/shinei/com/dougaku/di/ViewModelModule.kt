/*
 * Copyright (C) 2018 The Android Open Source Project
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

import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider

import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap
import shinei.com.dougaku.viewModel.*

@Suppress("unused")
@Module
abstract class ViewModelModule {
    @Binds
    @IntoMap
    @ViewModelKey(SharedViewModel::class)
    abstract fun bindSharedViewModel(sharedViewModel: SharedViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(MainActivityModel::class)
    abstract fun bindMainActivityModel(mainActivityModel: MainActivityModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(MainViewModel::class)
    abstract fun bindMainViewModel(mainViewModel: MainViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(AlbumPageModel::class)
    abstract fun bindAlbumPageModel(albumPageModel: AlbumPageModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(ArtistPageModel::class)
    abstract fun bindArtistPageModel(artistPageModel: ArtistPageModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(ProducerPageModel::class)
    abstract fun bindProducerPageModel(producerPageModel: ProducerPageModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(MyselfPageModel::class)
    abstract fun bindMyselfPageModel(myselfPageModel: MyselfPageModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(AlbumDetailModel::class)
    abstract fun bindAlbumDetailModel(albumDetailModel: AlbumDetailModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(ProducerDetailModel::class)
    abstract fun bindProducerDetailModel(producerDetailModel: ProducerDetailModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(ArtistDetailModel::class)
    abstract fun bindArtistDetailModel(artistDetailModel: ArtistDetailModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(LikedTracksModel::class)
    abstract fun bindLikedTracksModel(likedTracksModel: LikedTracksModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(LikedAlbumsModel::class)
    abstract fun bindLikedAlbumsModel(likedAlbumsModel: LikedAlbumsModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(MyPlaylistsModel::class)
    abstract fun bindMyPlaylistsModel(myPlaylistsModel: MyPlaylistsModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(MyPlaylistDetailModel::class)
    abstract fun bindMyPlaylistDetailModel(myPlaylistDetailModel: MyPlaylistDetailModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(ListeningHistoryModel::class)
    abstract fun bindListeningHistoryModel(listeningHistoryModel: ListeningHistoryModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(SearchViewModel::class)
    abstract fun bindSearchViewModel(searchViewModel: SearchViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(SearchSongModel::class)
    abstract fun bindSearchSongModel(searchSongModel: SearchSongModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(SearchArtistModel::class)
    abstract fun bindSearchArtistModel(searchArtistModel: SearchArtistModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(SearchAlbumModel::class)
    abstract fun bindSearchAlbumModel(searchAlbumModel: SearchAlbumModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(SearchProducerModel::class)
    abstract fun bindSearchProducerModel(searchProducerModel: SearchProducerModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(PlayerViewModel::class)
    abstract fun bindPlayerViewModel(playerViewModel: PlayerViewModel): ViewModel

    @Binds
    abstract fun bindViewModelFactory(viewModelFactory: ViewModelFactory): ViewModelProvider.Factory
}
