package com.f0x1d.logfox.network.service

import com.f0x1d.logfox.network.model.response.FoxBinCreatedDocumentResponse
import okhttp3.RequestBody
import retrofit2.Call

import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

interface FoxBinApiService {

    @Headers("Content-Type: application/json;charset=UTF-8")
    @POST("create")
    fun createDocument(@Body body: RequestBody): Call<FoxBinCreatedDocumentResponse>
}