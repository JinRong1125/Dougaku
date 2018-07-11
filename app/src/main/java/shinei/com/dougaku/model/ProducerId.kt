package shinei.com.dougaku.model

import com.google.gson.annotations.SerializedName

data class ProducerId(
        @SerializedName("id_producer") val producerId: Int): RequestCode()