package shinei.com.dougaku.model

import com.google.gson.annotations.SerializedName

data class Keyword(
        @SerializedName("keyword") val keyword: String): RequestCode()