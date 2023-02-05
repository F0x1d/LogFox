package com.f0x1d.logfox.model.request

import com.google.gson.annotations.SerializedName

data class FoxBinCreateDocumentRequest(
    @SerializedName("content") val content: String,
    @SerializedName("deleteAfter") val deleteAfter: Long = 0
)
