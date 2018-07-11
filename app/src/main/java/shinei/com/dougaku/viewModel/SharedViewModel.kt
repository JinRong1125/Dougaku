package shinei.com.dougaku.viewModel

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import shinei.com.dougaku.model.Album
import shinei.com.dougaku.model.Artist
import shinei.com.dougaku.model.Producer
import shinei.com.dougaku.model.Song
import shinei.com.dougaku.room.MyPlaylists
import javax.inject.Inject

class SharedViewModel @Inject constructor(): ViewModel() {

    val selectedAlbum = MutableLiveData<Album>()
    val selectedProducer = MutableLiveData<Producer>()
    val selectedArtist = MutableLiveData<Artist>()
    val selectedTrack = MutableLiveData<Int>()
    val selectedSongs = MutableLiveData<List<Song>>()
    val selectedPlaylist = MutableLiveData<MyPlaylists>()
    val enteredKeyword = MutableLiveData<String>()

    val likedTracksUpdated = MutableLiveData<Boolean>()
    val likedAlbumsUpdated = MutableLiveData<Boolean>()
    val myPlaylistUpdated = MutableLiveData<Boolean>()
    val historyTracksUpdated = MutableLiveData<Boolean>()
}