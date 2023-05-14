package com.f0x1d.logfox.model.network.response

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
data class FoxBinCreatedDocumentResponse(@SerializedName("slug") val slug: String)
