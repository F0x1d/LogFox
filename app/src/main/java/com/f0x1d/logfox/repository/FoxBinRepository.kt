package com.f0x1d.logfox.repository

import com.f0x1d.logfox.network.model.request.FoxBinCreateDocumentRequest
import com.f0x1d.logfox.network.model.response.FoxBinErrorResponse
import com.f0x1d.logfox.network.service.FoxBinApiService
import com.f0x1d.logfox.repository.base.BaseRepository
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FoxBinRepository @Inject constructor(private val foxBinApiService: FoxBinApiService, private val gson: Gson): BaseRepository() {

    companion object {
        const val FOXBIN_DOMAIN = "https://foxbin.f0x1d.com/"
    }

    suspend fun uploadViaApi(content: String) = withContext(Dispatchers.IO) {
        val response = foxBinApiService.createDocument(
            gson.toJson(FoxBinCreateDocumentRequest(content)).toRequestBody("application/json".toMediaType())
        ).execute()

        response.errorBody()?.apply {
            throw Exception(gson.fromJson(string(), FoxBinErrorResponse::class.java).error).also { close() }
        }

        response.body()?.apply {
            return@withContext FOXBIN_DOMAIN + slug
        }

        throw Exception(response.toString())
    }
}