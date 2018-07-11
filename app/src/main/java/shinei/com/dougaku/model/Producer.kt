package shinei.com.dougaku.model

import com.google.gson.annotations.SerializedName

data class Producer(
        @SerializedName("id_producer") val producerId: Int,
        @SerializedName("name") val name: String,
        @SerializedName("url_image") val imageUrl: String,
        @SerializedName("counts_album") val albumCounts: Int)