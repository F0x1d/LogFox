package com.f0x1d.logfox.repository.network.retrofit

import com.f0x1d.logfox.model.response.FoxBinCreatedDocumentResponse
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