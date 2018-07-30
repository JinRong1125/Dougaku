package shinei.com.dougaku.helper

const val PLAYER_ACTION = "shinei.com.dougaku.action."

const val TARGET_SONG = "TARGET_SONG"

const val PREPARE_TRACK = PLAYER_ACTION + "PREPARE_TRACK"
const val PLAY_PAUSE = PLAYER_ACTION + "PLAY_PAUSE"
const val PLAY = PLAYER_ACTION + "PLAY"
const val PAUSE = PLAYER_ACTION + "PAUSE"
const val STOP = PLAYER_ACTION + "STOP"
const val SEEK = PLAYER_ACTION + "SEEK"
const val PREPARE_PAUSE = PLAYER_ACTION + "PREPARE_PAUSE"

const val SEEK_TIME = "SEEK_TIME"
const val NEXT_TRACK = "NEXT_TRACK"
const val PREVIOUS_TRACK = "PREVIOUS_TRACK"

val INTENT_REQUEST_ID = 101
val PLAYER_NOTIFICATION_ID = 1001
val PLAYER_NOTIFICATION_CHANNEL_ID = "player_notification_channel_id"
val PLAYER_NOTIFICATION_CHANNEL_NAME = "player_notification_channel_name"

enum class PlayMode {
    NORMAL, SHUFFLE, REPEAT, LOOP, SHUFFLE_REPEAT
}



