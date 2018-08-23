package shinei.com.dougaku.service

import android.app.*
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.media.AudioAttributes
import android.media.AudioFocusRequest
import android.media.AudioManager
import android.media.session.PlaybackState
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkRequest
import android.net.Uri
import android.os.*
import android.support.annotation.RequiresApi
import android.support.v4.app.NotificationCompat
import android.support.v4.content.ContextCompat
import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.MediaBrowserServiceCompat
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.MediaButtonReceiver
import android.support.v4.media.session.MediaSessionCompat
import android.support.v4.media.session.PlaybackStateCompat
import android.text.TextUtils
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
import shinei.com.dougaku.view.activity.MainActivity
import java.util.concurrent.TimeUnit

class PlayerService: MediaBrowserServiceCompat() {

    var exoPlayer: ExoPlayer? = null
    var audioManager: AudioManager? = null
    var mediaSessionCompat: MediaSessionCompat? = null
    var progressDisposable: Disposable? = null
    var notificationManager: NotificationManager? = null
    var connectivityManager: ConnectivityManager? = null
    var networkCallback: NetworkCallback? = null

    val focusLock = Any()
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
        stopSelf()
    }

    override fun onDestroy() {
        super.onDestroy()
        finishService()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        MediaButtonReceiver.handleIntent(mediaSessionCompat, intent)
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onLoadChildren(parentId: String, result: Result<MutableList<MediaBrowserCompat.MediaItem>>) {
        result.sendResult(null)
    }

    override fun onGetRoot(clientPackageName: String, clientUid: Int, rootHints: Bundle?): BrowserRoot? {
        return if (TextUtils.equals(clientPackageName, packageName)) {
            MediaBrowserServiceCompat.BrowserRoot(getString(R.string.app_name), null)
        } else null
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
            val mediaSessionState = mediaSessionCompat!!.controller.playbackState
            if (playWhenReady) {
                if (mediaSessionState == null || mediaSessionState.state != PlaybackState.STATE_PLAYING) {
                    setPlaybackState(PlaybackStateCompat.STATE_PLAYING, exoPlayer!!.currentPosition)
                    updateNotification()
                    if (progressDisposable == null)
                        progressDisposable = Observable.interval(0, 1000, TimeUnit.MILLISECONDS)
                                .subscribe{
                                    setPlaybackState(PlaybackStateCompat.STATE_PLAYING, exoPlayer?.currentPosition)
                                }
                }
            }
            else {
                if (mediaSessionState == null || mediaSessionState.state != PlaybackState.STATE_PAUSED) {
                    progressDisposable?.dispose()
                    progressDisposable = null
                    setPlaybackState(PlaybackStateCompat.STATE_PAUSED, exoPlayer!!.currentPosition)
                    if (isNotificationActivated())
                        updateNotification()
                }
            }
            when (playbackState) {
                Player.STATE_IDLE -> {
                }
                Player.STATE_READY -> {
                    if (!isReady) {
                        isReady = true
                        setPlaybackState(PlaybackStateCompat.STATE_CONNECTING, exoPlayer!!.duration)
                        if (isPreparePause){
                            isPreparePause = false
                            pause()
                        }
                    }
                }
                Player.STATE_ENDED -> {
                    setPlaybackState(PlaybackStateCompat.STATE_NONE, 0)
                }
            }
        }
    }

    fun initialService() {
        audioManager = getSystemService(Context.AUDIO_SERVICE) as AudioManager
        notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        exoPlayer = ExoPlayerFactory.newSimpleInstance(application, DefaultTrackSelector())
        exoPlayer?.addListener(eventListener)
        networkCallback = NetworkCallback()
        connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        connectivityManager?.registerNetworkCallback(NetworkRequest.Builder().build(), networkCallback)

        mediaSessionCompat = MediaSessionCompat(applicationContext, resources.getString(R.string.app_name))
        mediaSessionCompat?.setFlags(MediaSessionCompat.FLAG_HANDLES_MEDIA_BUTTONS or MediaSessionCompat.FLAG_HANDLES_TRANSPORT_CONTROLS)
        mediaSessionCompat?.setCallback(mediaSessionCallback)
        mediaSessionCompat?.isActive = true
        sessionToken = mediaSessionCompat?.sessionToken
    }

    fun finishService() {
        exoPlayer?.release()
        notificationManager?.cancel(PLAYER_NOTIFICATION_ID)
        stopForeground(true)
        mediaSessionCompat?.isActive = false
        mediaSessionCompat?.release()
        progressDisposable?.dispose()
        connectivityManager?.unregisterNetworkCallback(networkCallback)
    }

    val mediaSessionCallback = object : MediaSessionCompat.Callback() {

        override fun onPlayFromUri(uri: Uri, extras: Bundle) {
            super.onPlayFromUri(uri, extras)
            isReady = false
            createNotification(extras)
            exoPlayer?.prepare(ExtractorMediaSource.Factory((application as App).defaultDataSourceFactory)
                    .createMediaSource(uri))
        }

        override fun onPlay() {
            super.onPlay()
            requestPlay()
        }

        override fun onPause() {
            super.onPause()
            pause()
        }

        override fun onStop() {
            super.onStop()
            stop()
        }

        override fun onSeekTo(pos: Long) {
            super.onSeekTo(pos)
            exoPlayer?.seekTo(pos)
        }

        override fun onSkipToPrevious() {
            super.onSkipToPrevious()
            setPlaybackState(PlaybackStateCompat.STATE_SKIPPING_TO_PREVIOUS, 0)
        }

        override fun onSkipToNext() {
            super.onSkipToNext()
            setPlaybackState(PlaybackStateCompat.STATE_SKIPPING_TO_NEXT, 0)
        }

        override fun onCustomAction(action: String, extras: Bundle) {
            super.onCustomAction(action, extras)
            when (action) {
                UPDATE_METADATA -> {
                    if (isNotificationActivated() or exoPlayer!!.playWhenReady)
                        updateNotification()
                }
                PLAY_PAUSE -> {
                    if (!exoPlayer!!.playWhenReady)
                        requestPlay()
                    else
                        pause()
                }
                PREPARE_PAUSE ->
                    isPreparePause = true
            }
        }
    }

    inner class NetworkCallback: ConnectivityManager.NetworkCallback() {
        override fun onAvailable(network: Network) {
            super.onAvailable(network)
            if (isError) {
                isError = false
                val position = exoPlayer?.currentPosition
                exoPlayer?.prepare(ExtractorMediaSource.Factory((application as App).defaultDataSourceFactory)
                        .createMediaSource(mediaSessionCompat!!.controller.metadata.description.mediaUri))
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

    fun stop() {
        exoPlayer?.stop()
    }

    fun setPlaybackState(playbackState: Int, position: Long?) {
        val playbackStateBuilder = PlaybackStateCompat.Builder()
        playbackStateBuilder.setActions(
                PlaybackStateCompat.ACTION_PLAY_PAUSE or
                        PlaybackStateCompat.ACTION_PLAY or
                        PlaybackStateCompat.ACTION_PAUSE or
                        PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS or
                        PlaybackStateCompat.ACTION_SKIP_TO_NEXT)

        if (position != null) {
            playbackStateBuilder.setState(playbackState, position, 0f)
            mediaSessionCompat?.setPlaybackState(playbackStateBuilder.build())
        }
    }

    fun createNotification(bundle: Bundle) {
        val newCoverUrl = bundle.getString(TARGET_COVER_URL)
        val metadata = mediaSessionCompat!!.controller.metadata
        if (metadata != null) {
            val description = metadata.description
            if (newCoverUrl == description.iconUri.toString()) {
                setMediaMetadata(bundle, description.iconBitmap!!)
                return
            }
        }
        Glide.with(this)
                .asBitmap()
                .load(newCoverUrl)
                .into(object : SimpleTarget<Bitmap>() {
                    override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                        setMediaMetadata(bundle, resource)
                    }
                })
    }

    fun setMediaMetadata(bundle: Bundle, resource: Bitmap) {
        val mediaMetadataCompat = MediaMetadataCompat.Builder()
        mediaMetadataCompat.putBitmap(MediaMetadataCompat.METADATA_KEY_ALBUM_ART, resource)
        mediaMetadataCompat.putBitmap(MediaMetadataCompat.METADATA_KEY_DISPLAY_ICON, resource)
        mediaMetadataCompat.putString(MediaMetadataCompat.METADATA_KEY_MEDIA_ID, bundle.getString(TARGET_ID))
        mediaMetadataCompat.putString(MediaMetadataCompat.METADATA_KEY_DISPLAY_TITLE, bundle.getString(TARGET_TITLE))
        mediaMetadataCompat.putString(MediaMetadataCompat.METADATA_KEY_DISPLAY_SUBTITLE, bundle.getString(TARGET_ALBUM))
        mediaMetadataCompat.putString(MediaMetadataCompat.METADATA_KEY_MEDIA_URI, bundle.getString(TARGET_STREAM_URL))
        mediaMetadataCompat.putString(MediaMetadataCompat.METADATA_KEY_DISPLAY_ICON_URI, bundle.getString(TARGET_COVER_URL))
        mediaSessionCompat?.setMetadata(mediaMetadataCompat.build())
    }

    fun updateNotification() {
        val controller = mediaSessionCompat!!.controller
        if (controller.metadata != null) {
            val description = controller.metadata.description
            val playBackState = controller.playbackState.state
            val applicationIntent = PendingIntent.getActivity(
                    this, INTENT_REQUEST_ID, Intent(this, MainActivity::class.java), 0)
            val style = android.support.v4.media.app.NotificationCompat.MediaStyle()
                    .setMediaSession(mediaSessionCompat?.sessionToken)
                    .setShowActionsInCompactView(0, 1, 2)
            val notification = NotificationCompat.Builder(applicationContext, PLAYER_NOTIFICATION_CHANNEL_ID)
                    .setColor(ContextCompat.getColor(applicationContext, R.color.green_half))
                    .setSmallIcon(R.drawable.icon_app_24dp)
                    .setLargeIcon(description.iconBitmap)
                    .setContentIntent(applicationIntent)
                    .setContentTitle(description.title)
                    .setContentText(description.subtitle)
                    .setWhen(0)
                    .setStyle(style)
            if (playBackState == PlaybackStateCompat.STATE_PLAYING)
                playingNotification(notification)
            else
                pausedNotification(notification)
        }
    }

    fun playingNotification(notification: NotificationCompat.Builder) {
        notification
                .addAction(createAction(R.drawable.icon_previous_24dp, PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS))
                .addAction(createAction(R.drawable.icon_pause_24dp, PlaybackStateCompat.ACTION_PAUSE))
                .addAction(createAction(R.drawable.icon_next_24dp, PlaybackStateCompat.ACTION_SKIP_TO_NEXT))
                .setOngoing(true)
        notifyNotification(notification)
    }

    fun pausedNotification(notification: NotificationCompat.Builder) {
        notification
                .addAction(createAction(R.drawable.icon_previous_24dp, PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS))
                .addAction(createAction(R.drawable.icon_play_24dp, PlaybackStateCompat.ACTION_PLAY))
                .addAction(createAction(R.drawable.icon_next_24dp, PlaybackStateCompat.ACTION_SKIP_TO_NEXT))
                .setOngoing(false)
        notifyNotification(notification)
    }

    fun createAction(resourceId: Int, playbackState: Long): NotificationCompat.Action {
        return NotificationCompat.Action(resourceId, "",
                MediaButtonReceiver.buildMediaButtonPendingIntent(this, playbackState))
    }

    fun notifyNotification(notification: NotificationCompat.Builder) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChannel = NotificationChannel(PLAYER_NOTIFICATION_CHANNEL_ID, PLAYER_NOTIFICATION_CHANNEL_NAME, NotificationManager.IMPORTANCE_LOW)
            notificationChannel.enableVibration(false)
            notificationChannel.enableLights(false)
            notificationManager?.createNotificationChannel(notificationChannel)
        }
        val buildNotification = notification.build()
        notificationManager?.notify(PLAYER_NOTIFICATION_ID, buildNotification)
        startForeground(PLAYER_NOTIFICATION_ID, buildNotification)
    }

    fun isNotificationActivated(): Boolean {
        return notificationManager!!.activeNotifications.isNotEmpty()
    }
}