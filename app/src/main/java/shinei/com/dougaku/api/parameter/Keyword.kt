package shinei.com.dougaku.api.parameter

import com.google.gson.annotations.SerializedName

data class Keyword(
        @SerializedName("keyword") val keyword: String): RequestCode()