package com.f0x1d.logfox.model.request

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
data class FoxBinCreateDocumentRequest(
    @SerializedName("content") val content: String,
    @SerializedName("deleteAfter") val deleteAfter: Long = 0
)
