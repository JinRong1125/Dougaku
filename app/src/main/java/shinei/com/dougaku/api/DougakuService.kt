package shinei.com.dougaku.api

import io.reactivex.Observable
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST
import shinei.com.dougaku.model.*

interface DougakuService {
    companion object {
        const val BASE_URL = "http://192.168.1.105:8080/"
    }

    @POST("api/albums")
    fun loadAlbums(@Body body: RequestObject): Observable<List<Album>>

    @POST("api/producers")
    fun loadProducers(@Body body: RequestObject): Observable<List<Producer>>

    @POST("api/artists")
    fun loadArtists(@Body body: RequestObject): Observable<List<Artist>>

    @Headers("Content-Type: application/json")
    @POST("api/album/songs")
    fun loadAlbumSongs(@Body body: AlbumId): Observable<List<Song>>

    @Headers("Content-Type: application/json")
    @POST("api/artist/songs_albums")
    fun loadArtistSongsAlbums(@Body body: ArtistId): Observable<SongAlbum>

    @Headers("Content-Type: application/json")
    @POST("api/producer/albums")
    fun loadProducerAlbums(@Body body: ProducerId): Observable<List<Album>>

    @Headers("Content-Type: application/json")
    @POST("api/search/songs")
    fun searchSongs(@Body body: Keyword): Observable<List<Song>>

    @Headers("Content-Type: application/json")
    @POST("api/search/albums")
    fun searchAlbums(@Body body: Keyword): Observable<List<Album>>

    @Headers("Content-Type: application/json")
    @POST("api/search/artists")
    fun searchArtists(@Body body: Keyword): Observable<List<Artist>>

    @Headers("Content-Type: application/json")
    @POST("api/search/producers")
    fun searchProducers(@Body body: Keyword): Observable<List<Producer>>
}