package shinei.com.dougaku.model

import com.google.gson.annotations.SerializedName

abstract class RequestCode(
        @SerializedName("code_request") val requestCode: String = "*")