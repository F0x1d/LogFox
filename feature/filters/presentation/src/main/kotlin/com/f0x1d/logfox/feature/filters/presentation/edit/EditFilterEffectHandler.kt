package com.f0x1d.logfox.feature.filters.presentation.edit

import android.content.Context
import com.f0x1d.logfox.core.di.IODispatcher
import com.f0x1d.logfox.core.tea.EffectHandler
import com.f0x1d.logfox.feature.filters.api.domain.CreateFilterUseCase
import com.f0x1d.logfox.feature.filters.api.domain.GetFilterByIdFlowUseCase
import com.f0x1d.logfox.feature.filters.api.domain.UpdateFilterUseCase
import com.google.gson.Gson
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.withContext
import javax.inject.Inject

internal class EditFilterEffectHandler @Inject constructor(
    @ApplicationContext private val context: Context,
    private val getFilterByIdFlowUseCase: GetFilterByIdFlowUseCase,
    private val createFilterUseCase: CreateFilterUseCase,
    private val updateFilterUseCase: UpdateFilterUseCase,
    private val gson: Gson,
    @IODispatcher private val ioDispatcher: CoroutineDispatcher,
) : EffectHandler<EditFilterSideEffect, EditFilterCommand> {

    override suspend fun handle(
        effect: EditFilterSideEffect,
        onCommand: suspend (EditFilterCommand) -> Unit,
    ) {
        when (effect) {
            is EditFilterSideEffect.LoadFilter -> {
                getFilterByIdFlowUseCase(effect.filterId ?: -1L)
                    .distinctUntilChanged()
                    .take(1)
                    .collect { filter ->
                        if (filter != null) {
                            onCommand(EditFilterCommand.FilterLoaded(filter))
                        }
                    }
            }

            is EditFilterSideEffect.SaveFilter -> {
                if (effect.filter == null) {
                    createFilterUseCase(
                        including = effect.including,
                        enabled = effect.enabled,
                        enabledLogLevels = effect.enabledLogLevels,
                        uid = effect.uid,
                        pid = effect.pid,
                        tid = effect.tid,
                        packageName = effect.packageName,
                        tag = effect.tag,
                        content = effect.content,
                    )
                } else {
                    updateFilterUseCase(
                        userFilter = effect.filter,
                        including = effect.including,
                        enabled = effect.enabled,
                        enabledLogLevels = effect.enabledLogLevels,
                        uid = effect.uid,
                        pid = effect.pid,
                        tid = effect.tid,
                        packageName = effect.packageName,
                        tag = effect.tag,
                        content = effect.content,
                    )
                }
            }

            is EditFilterSideEffect.ExportFilter -> {
                withContext(ioDispatcher) {
                    runCatching {
                        context.contentResolver.openOutputStream(effect.uri)?.use { outputStream ->
                            val filters = listOfNotNull(effect.filter)
                            outputStream.write(gson.toJson(filters).encodeToByteArray())
                        }
                    }
                }
            }

            // UI side effects - not handled here
            is EditFilterSideEffect.UpdatePackageNameField -> Unit
        }
    }
}
