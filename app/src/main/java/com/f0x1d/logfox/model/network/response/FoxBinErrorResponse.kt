package com.f0x1d.logfox.model.network.response

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
data class FoxBinErrorResponse(@SerializedName("error") val error: String)
