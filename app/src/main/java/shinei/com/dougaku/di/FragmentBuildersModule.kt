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

import dagger.Module
import dagger.android.ContributesAndroidInjector
import shinei.com.dougaku.view.base.FrameFragment
import shinei.com.dougaku.view.base.PageFragment
import shinei.com.dougaku.view.fragment.*

@Suppress("unused")
@Module
abstract class FragmentBuildersModule {
    @ContributesAndroidInjector
    abstract fun contributePageFragment(): PageFragment

    @ContributesAndroidInjector
    abstract fun contributeFrameFragment(): FrameFragment

    @ContributesAndroidInjector
    abstract fun contributeMainFragment(): MainFragment

    @ContributesAndroidInjector
    abstract fun contributeAlbumFragment(): AlbumFragment

    @ContributesAndroidInjector
    abstract fun contributeArtistFragment(): ArtistFragment

    @ContributesAndroidInjector
    abstract fun contributeProducerFragment(): ProducerFragment

    @ContributesAndroidInjector
    abstract fun contributeMyselfFragment(): MyselfFragment

    @ContributesAndroidInjector
    abstract fun contributeAlbumDetailFragment(): AlbumDetailFragment

    @ContributesAndroidInjector
    abstract fun contributeProducerDetailFragment(): ProducerDetailFragment

    @ContributesAndroidInjector
    abstract fun contributeArtistDetailFragment(): ArtistDetailFragment

    @ContributesAndroidInjector
    abstract fun contributeLikedTracksFragment(): LikedTracksFragment

    @ContributesAndroidInjector
    abstract fun contributeLikedAlbumsFragment(): LikedAlbumsFragment

    @ContributesAndroidInjector
    abstract fun contributeMyPlaylistsFragment(): MyPlaylistsFragment

    @ContributesAndroidInjector
    abstract fun contributeMyPlaylistDetailFragment(): MyPlaylistDetailFragment

    @ContributesAndroidInjector
    abstract fun contributeListeningHistoryFragment(): ListeningHistoryFragment

    @ContributesAndroidInjector
    abstract fun contributeSearchFrgment(): SearchFragment

    @ContributesAndroidInjector
    abstract fun contributeSearchSongFragment(): SearchSongFragment

    @ContributesAndroidInjector
    abstract fun contributeSearchArtistFragment(): SearchArtistFragment

    @ContributesAndroidInjector
    abstract fun contributeSearchAlbumFragment(): SearchAlbumFragment

    @ContributesAndroidInjector
    abstract fun contributeSearchProducerFragment(): SearchProducerFragment

    @ContributesAndroidInjector
    abstract fun contributePlayerFragment(): PlayerFragment
}
