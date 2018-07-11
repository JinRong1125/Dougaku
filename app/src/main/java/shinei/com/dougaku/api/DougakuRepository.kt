package shinei.com.dougaku.api

import android.content.Context
import io.reactivex.Observable
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import shinei.com.dougaku.R
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
        return dougakuService.loadAlbums(RequestObject()).retry(3)
    }

    fun loadProducers(): Observable<List<Producer>> {
        return dougakuService.loadProducers(RequestObject()).retry(3)
    }

    fun loadArtists(): Observable<List<Artist>> {
        return dougakuService.loadArtists(RequestObject()).retry(3)
    }

    fun loadAlbumSongs(albumId: Int): Observable<List<Song>> {
        return dougakuService.loadAlbumSongs(AlbumId(albumId)).retry(3)
    }

    fun loadArtistSongsAlbums(artistId: Int): Observable<SongAlbum> {
        return dougakuService.loadArtistSongsAlbums(ArtistId(artistId)).retry(3)
    }

    fun loadProducerAlbums(producerId: Int): Observable<List<Album>> {
        return dougakuService.loadProducerAlbums(ProducerId(producerId)).retry(3)
    }

    fun searchSongs(keyword: String): Observable<List<Song>> {
        return dougakuService.searchSongs(Keyword(keyword)).retry(3)
    }

    fun searchAlbums(keyword: String): Observable<List<Album>> {
        return dougakuService.searchAlbums(Keyword(keyword)).retry(3)
    }

    fun searchArtists(keyword: String): Observable<List<Artist>> {
        return dougakuService.searchArtists(Keyword(keyword)).retry(3)
    }

    fun searchProducers(keyword: String): Observable<List<Producer>> {
        return dougakuService.searchProducers(Keyword(keyword)).retry(3)
    }

    fun showError(context: Context) {
        Utils.showToast(context, context.getString(R.string.toast_connect_failed))
    }
}