package shinei.com.dougaku.helper

const val PLAYER_ACTION = "shinei.com.dougaku.action."

const val TARGET_TITLE = "TARGET_TITLE"
const val TARGET_ALBUM = "TARGET_ALBUM"
const val TARGET_COVER_URL = "TARGET_COVER_URL"
const val TARGET_STREAM_URL = "TARGET_STREAM_URL"

const val PLAY_PAUSE = PLAYER_ACTION + "PLAY_PAUSE"
const val PREPARE_PAUSE = PLAYER_ACTION + "PREPARE_PAUSE"

val INTENT_REQUEST_ID = 101
val PLAYER_NOTIFICATION_ID = 1001
val PLAYER_NOTIFICATION_CHANNEL_ID = "player_notification_channel_id"
val PLAYER_NOTIFICATION_CHANNEL_NAME = "player_notification_channel_name"

enum class PlayMode {
    NORMAL, SHUFFLE, REPEAT, LOOP, SHUFFLE_REPEAT
}



