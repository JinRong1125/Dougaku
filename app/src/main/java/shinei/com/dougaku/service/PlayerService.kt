package shinei.com.dougaku.service

import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.media.AudioAttributes
import android.media.AudioFocusRequest
import android.media.AudioManager
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkRequest
import android.net.Uri
import android.os.Binder
import android.os.Build
import android.os.IBinder
import android.support.annotation.RequiresApi
import android.support.v4.app.NotificationCompat
import android.support.v4.content.ContextCompat
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.MediaSessionCompat
import android.support.v4.media.session.PlaybackStateCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.SimpleTarget
import com.bumptech.glide.request.transition.Transition
import com.google.android.exoplayer2.*
import com.google.android.exoplayer2.source.ExtractorMediaSource
import com.google.android.exoplayer2.source.TrackGroupArray
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector
import com.google.android.exoplayer2.trackselection.TrackSelectionArray
import io.reactivex.Observable
import io.reactivex.disposables.Disposable
import shinei.com.dougaku.App
import shinei.com.dougaku.R
import shinei.com.dougaku.helper.*
import shinei.com.dougaku.model.Song
import shinei.com.dougaku.view.activity.MainActivity
import java.util.concurrent.TimeUnit

class PlayerService: Service() {

    var playerListener: PlayerListener? = null
    var exoPlayer: ExoPlayer? = null
    var audioManager: AudioManager? = null
    var mediaSessionCompat: MediaSessionCompat? = null
    var notificationManager: NotificationManager? = null
    var progressDisposable: Disposable? = null
    var connectivityManager: ConnectivityManager? = null
    var networkCallback: NetworkCallback? = null

    val focusLock = Any()
    var currentSong: Song? = null
    var albumBitmap: Bitmap? = null
    var isReady = false
    var isError = false
    var isPreparePause = false
    var playbackDelayed = false
    var resumeOnFocusGain = false

    override fun onCreate() {
        super.onCreate()
        initialService()
    }

    override fun onTaskRemoved(rootIntent: Intent) {
        super.onTaskRemoved(rootIntent)
        exoPlayer?.stop()
        exoPlayer?.release()
        exoPlayer = null
        notificationManager?.cancel(PLAYER_NOTIFICATION_ID)
        connectivityManager!!.unregisterNetworkCallback(networkCallback)
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        when (intent.action) {
            PREPARE_TRACK -> {
                isReady = false
                currentSong = intent.getParcelableExtra(TARGET_SONG)
                createNotification(exoPlayer!!.playWhenReady)
                exoPlayer?.prepare(ExtractorMediaSource.Factory((application as App).defaultDataSourceFactory)
                        .createMediaSource(Uri.parse(currentSong?.streamUrl)))
            }
            PLAY_PAUSE -> {
                if (exoPlayer!!.playWhenReady)
                    pause()
                else
                    requestPlay()
            }
            PLAY -> exoPlayer?.playWhenReady = true
            PAUSE -> exoPlayer?.playWhenReady = false
            STOP -> exoPlayer?.stop()
            SEEK -> exoPlayer?.seekTo(intent.getIntExtra(SEEK_TIME, 0).toLong())
            PREPARE_PAUSE -> isPreparePause = true
            NEXT_TRACK -> playerListener?.onNextTrack()
            PREVIOUS_TRACK -> playerListener?.onPreviousTrack()
        }
        return START_NOT_STICKY
    }

    override fun onBind(intent: Intent): IBinder {
        return PlayerBinder()
    }

    inner class PlayerBinder: Binder() {
        fun getService(): PlayerService {
            return this@PlayerService
        }
    }

    val onAudioFocusChangeListener = AudioManager.OnAudioFocusChangeListener { focusChange ->
        when (focusChange) {
            AudioManager.AUDIOFOCUS_GAIN -> if (playbackDelayed || resumeOnFocusGain) {
                synchronized(focusLock) {
                    playbackDelayed = false
                    resumeOnFocusGain = false
                }
                play()
            }
            AudioManager.AUDIOFOCUS_LOSS -> {
                synchronized(focusLock) {
                    resumeOnFocusGain = false
                    playbackDelayed = false
                }
                pause()
            }
            AudioManager.AUDIOFOCUS_LOSS_TRANSIENT, AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK -> {
                synchronized(focusLock) {
                    resumeOnFocusGain = exoPlayer!!.playWhenReady
                    playbackDelayed = false
                }
                pause()
            }
        }
    }

    val eventListener = object : Player.EventListener {
        override fun onPlaybackParametersChanged(playbackParameters: PlaybackParameters) {}

        override fun onSeekProcessed() {}

        override fun onTracksChanged(trackGroups: TrackGroupArray, trackSelections: TrackSelectionArray) {}

        override fun onPlayerError(error: ExoPlaybackException) {
            isError = true
        }

        override fun onLoadingChanged(isLoading: Boolean) {}

        override fun onPositionDiscontinuity(reason: Int) {}

        override fun onRepeatModeChanged(repeatMode: Int) {}

        override fun onShuffleModeEnabledChanged(shuffleModeEnabled: Boolean) {}

        override fun onTimelineChanged(timeline: Timeline, manifest: Any?, reason: Int) {}

        override fun onPlayerStateChanged(playWhenReady: Boolean, playbackState: Int) {
            if (playWhenReady) {
                playerListener?.onPlay()
                updateNotification(PlaybackStateCompat.STATE_PLAYING)
                if (progressDisposable == null)
                    progressDisposable = Observable.interval(0, 1000, TimeUnit.MILLISECONDS)
                            .subscribe{ playerListener?.onProgressUpdated(exoPlayer!!.currentPosition.toInt()) }
            }
            else {
                if (isNotificationActivated())
                    updateNotification(PlaybackStateCompat.STATE_PAUSED)
                playerListener?.onPause()
                progressDisposable?.dispose()
                progressDisposable = null
            }
            when (playbackState) {
                Player.STATE_IDLE -> {
                }
                Player.STATE_READY -> {
                    if (!isReady) {
                        isReady = true
                        playerListener?.onReady(exoPlayer!!.duration.toInt())
                        if (isPreparePause){
                            isPreparePause = false
                            pause()
                        }
                    }
                }
                Player.STATE_BUFFERING ->
                    playerListener?.onBuffering()
                Player.STATE_ENDED ->
                    playerListener?.onEnded()
            }
        }
    }

    interface PlayerListener {
        fun onPlay()
        fun onPause()
        fun onBuffering()
        fun onReady(duration: Int)
        fun onEnded()
        fun onProgressUpdated(position: Int)
        fun onNextTrack()
        fun onPreviousTrack()
    }

    fun setPlayerListner(playerListener: PlayerListener) {
        this.playerListener = playerListener
    }

    fun initialService() {
        audioManager = getSystemService(Context.AUDIO_SERVICE) as AudioManager
        mediaSessionCompat = MediaSessionCompat(this, resources.getString(R.string.app_name))
        mediaSessionCompat?.setFlags(MediaSessionCompat.FLAG_HANDLES_TRANSPORT_CONTROLS or MediaSessionCompat.FLAG_HANDLES_MEDIA_BUTTONS)
        notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        exoPlayer = ExoPlayerFactory.newSimpleInstance(application, DefaultTrackSelector())
        exoPlayer?.addListener(eventListener)
        networkCallback = NetworkCallback()
        connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        connectivityManager?.registerNetworkCallback(NetworkRequest.Builder().build(), networkCallback)
    }

    inner class NetworkCallback: ConnectivityManager.NetworkCallback() {
        override fun onAvailable(network: Network) {
            super.onAvailable(network)
            if (isError) {
                isError = false
                val position = exoPlayer?.currentPosition
                exoPlayer?.prepare(ExtractorMediaSource.Factory((application as App).defaultDataSourceFactory)
                        .createMediaSource(Uri.parse(currentSong?.streamUrl)))
                exoPlayer?.seekTo(position!!)
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun requestAudioFocusO() {
        val playBackAttributes = AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_MEDIA)
                .setContentType(AudioAttributes.CONTENT_TYPE_SPEECH)
                .build()
        val focusRequest = AudioFocusRequest.Builder(AudioManager.AUDIOFOCUS_GAIN)
                .setAudioAttributes(playBackAttributes)
                .setAcceptsDelayedFocusGain(true)
                .setWillPauseWhenDucked(true)
                .setOnAudioFocusChangeListener(onAudioFocusChangeListener)
                .build()

        processFocusResult(audioManager?.requestAudioFocus(focusRequest))
    }

    fun processFocusResult(result: Int?) {
        synchronized(focusLock) {
            when (result) {
                AudioManager.AUDIOFOCUS_REQUEST_FAILED -> playbackDelayed = false
                AudioManager.AUDIOFOCUS_REQUEST_GRANTED -> {
                    playbackDelayed = false
                    play()
                }
                AudioManager.AUDIOFOCUS_REQUEST_DELAYED -> playbackDelayed = true
                else -> null
            }
        }
    }
    
    fun requestPlay() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
            requestAudioFocusO()
        else
            processFocusResult(audioManager?.requestAudioFocus(
                    onAudioFocusChangeListener, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN))
    }

    fun play() {
        exoPlayer?.playWhenReady = true
    }
    
    fun pause() {
        exoPlayer?.playWhenReady = false
    }

    fun createNotification(playWhenReady: Boolean) {
        Glide.with(this)
                .asBitmap()
                .load(currentSong!!.coverUrl)
                .into(object : SimpleTarget<Bitmap>() {
                    override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                        albumBitmap = resource
                        val mediaMetadataCompat = MediaMetadataCompat.Builder()
                        mediaMetadataCompat.putBitmap(MediaMetadataCompat.METADATA_KEY_ALBUM_ART, resource)
                        mediaMetadataCompat.putBitmap(MediaMetadataCompat.METADATA_KEY_DISPLAY_ICON, resource)
                        mediaSessionCompat?.setMetadata(mediaMetadataCompat.build())
                        if (isNotificationActivated() or playWhenReady)
                            updateNotification(PlaybackStateCompat.STATE_CONNECTING)
                    }
                })
    }

    fun updateNotification(playBackState: Int) {
        mediaSessionCompat?.setPlaybackState(PlaybackStateCompat.Builder()
                .setState(playBackState, 0, 1.0f).build())
        val applicationIntent = PendingIntent.getActivity(
                this, INTENT_REQUEST_ID, Intent(this, MainActivity::class.java), 0)
        val playpauseDrawable = if (playBackState != PlaybackStateCompat.STATE_PLAYING)
            R.drawable.icon_play_24dp else R.drawable.icon_pause_24dp
        val style = android.support.v4.media.app.NotificationCompat.MediaStyle()
                .setMediaSession(mediaSessionCompat?.sessionToken)
                .setShowActionsInCompactView(0, 1, 2, 3)
        val notification = NotificationCompat.Builder(applicationContext, PLAYER_NOTIFICATION_CHANNEL_ID)
                .setColor(ContextCompat.getColor(applicationContext, R.color.green_half))
                .setSmallIcon(R.drawable.icon_small)
                .setLargeIcon(albumBitmap)
                .setContentIntent(applicationIntent)
                .setContentTitle(currentSong?.title)
                .setContentText(currentSong?.artist)
                .setWhen(0)
                .addAction(createAction(PREVIOUS_TRACK, R.drawable.icon_previous_24dp))
                .addAction(createAction(PLAY_PAUSE, playpauseDrawable))
                .addAction(createAction(NEXT_TRACK, R.drawable.icon_next_24dp))
                .setStyle(style)
                .setOngoing(playBackState == PlaybackStateCompat.STATE_PLAYING)
                .build()
        notificationManager?.notify(PLAYER_NOTIFICATION_ID, notification)
    }

    fun createAction(action: String, drawable: Int): NotificationCompat.Action {
        val intent = Intent(this, this::class.java).apply { this.action = action }
        return NotificationCompat.Action.Builder(drawable, "",
                PendingIntent.getService(this, 0, intent, 0)).build()
    }

    fun isNotificationActivated(): Boolean {
        return notificationManager!!.activeNotifications.isNotEmpty()
    }
}