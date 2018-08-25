package shinei.com.dougaku.api.parameter

import com.google.gson.annotations.SerializedName

abstract class RequestCode(
        @SerializedName("code_request") val requestCode: String = "Περσεφόνη")