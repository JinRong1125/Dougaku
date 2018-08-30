package shinei.com.dougaku.api

import android.content.Context
import io.reactivex.Observable
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import shinei.com.dougaku.R
import shinei.com.dougaku.api.parameter.*
import shinei.com.dougaku.helper.RxThreadCallAdapterFactory
import shinei.com.dougaku.helper.Utils
import shinei.com.dougaku.model.*

class DougakuRepository {

    val dougakuService = Retrofit.Builder()
            .baseUrl(DougakuService.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(RxThreadCallAdapterFactory.create())
            .build().create(DougakuService::class.java)

    fun loadAlbums(): Observable<List<Album>> {
        return dougakuService.loadAlbums(RequestObject())
    }

    fun loadAlbums(albumId: AlbumId): Observable<List<Album>> {
        return dougakuService.loadAlbums(albumId)
    }

    fun loadProducers(): Observable<List<Producer>> {
        return dougakuService.loadProducers(RequestObject())
    }

    fun loadProducers(producerId: ProducerId): Observable<List<Producer>> {
        return dougakuService.loadProducers(producerId)
    }

    fun loadArtists(artistItem: ArtistItem): Observable<List<Artist>> {
        return dougakuService.loadArtists(artistItem)
    }

    fun loadArtists(artistName: ArtistName): Observable<List<Artist>> {
        return dougakuService.loadArtists(artistName)
    }

    fun loadAlbumSongs(albumId: Int): Observable<List<Song>> {
        return dougakuService.loadAlbumSongs(AlbumId(albumId))
    }

    fun loadArtistSongsAlbums(artistId: Int): Observable<SongAlbum> {
        return dougakuService.loadArtistSongsAlbums(ArtistId(artistId))
    }

    fun loadProducerAlbums(producerId: Int): Observable<List<Album>> {
        return dougakuService.loadProducerAlbums(ProducerId(producerId))
    }

    fun searchSongs(keyword: String): Observable<List<Song>> {
        return dougakuService.searchSongs(Keyword(keyword))
    }

    fun searchAlbums(keyword: String): Observable<List<Album>> {
        return dougakuService.searchAlbums(Keyword(keyword))
    }

    fun searchArtists(keyword: String): Observable<List<Artist>> {
        return dougakuService.searchArtists(Keyword(keyword))
    }

    fun searchProducers(keyword: String): Observable<List<Producer>> {
        return dougakuService.searchProducers(Keyword(keyword))
    }

    fun showError(context: Context) {
        Utils.showToast(context, context.getString(R.string.toast_connect_failed))
    }
}