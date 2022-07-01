package com.f0x1d.logfox.network.model.response

import com.google.gson.annotations.SerializedName

data class FoxBinErrorResponse(@SerializedName("error") val error: String)
