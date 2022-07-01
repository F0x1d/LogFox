package com.f0x1d.logfox.repository

import com.f0x1d.logfox.network.model.request.FoxBinCreateDocumentRequest
import com.f0x1d.logfox.network.service.FoxBinApiService
import com.f0x1d.logfox.repository.base.BaseRepository
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import javax.inject.Inject

class FoxBinRepository @Inject constructor(private val foxBinApiService: FoxBinApiService, private val gson: Gson): BaseRepository() {

    companion object {
        const val FOXBIN_DOMAIN = "https://foxbin.f0x1d.com/"
    }

    suspend fun uploadViaApi(content: String) = withContext(Dispatchers.IO) {
        val response = foxBinApiService.createDocument(
            gson.toJson(FoxBinCreateDocumentRequest(content)).toRequestBody("application/json".toMediaType())
        ).execute()

        return@withContext FOXBIN_DOMAIN + (response.body()?.slug ?: throw Exception(response.message()))
    }
}