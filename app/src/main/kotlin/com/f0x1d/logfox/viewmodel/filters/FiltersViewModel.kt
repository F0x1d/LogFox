package com.f0x1d.logfox.viewmodel.filters

import android.app.Application
import android.net.Uri
import androidx.lifecycle.asLiveData
import com.f0x1d.logfox.arch.viewmodel.BaseViewModel
import com.f0x1d.logfox.database.AppDatabase
import com.f0x1d.logfox.database.entity.UserFilter
import com.f0x1d.logfox.repository.logging.FiltersRepository
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

@HiltViewModel
class FiltersViewModel @Inject constructor(
    private val database: AppDatabase,
    private val filtersRepository: FiltersRepository,
    private val gson: Gson,
    application: Application
): BaseViewModel(application) {

    val filters = database.userFilterDao().getAllAsFlow()
        .distinctUntilChanged()
        .flowOn(Dispatchers.IO)
        .asLiveData()

    fun import(uri: Uri) = launchCatching(Dispatchers.IO) {
        ctx.contentResolver.openInputStream(uri)?.use {
            val filters = gson.fromJson<List<UserFilter>>(
                it.readBytes().decodeToString(),
                object : TypeToken<List<UserFilter>>() {}.type
            )

            filtersRepository.createAll(filters)
        }
    }

    fun exportAll(uri: Uri) = launchCatching(Dispatchers.IO) {
        val filters = filters.value ?: return@launchCatching

        ctx.contentResolver.openOutputStream(uri)?.use {
            it.write(gson.toJson(filters).encodeToByteArray())
        }
    }

    fun switch(userFilter: UserFilter, checked: Boolean) = filtersRepository.switch(userFilter, checked)
    fun delete(userFilter: UserFilter) = filtersRepository.delete(userFilter)
    fun clearAll() = filtersRepository.clear()
}
