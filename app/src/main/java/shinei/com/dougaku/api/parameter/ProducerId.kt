package shinei.com.dougaku.api.parameter

import com.google.gson.annotations.SerializedName

data class ProducerId(
        @SerializedName("id_producer") val producerId: Int): RequestCode()