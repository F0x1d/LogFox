package com.f0x1d.logfox.feature.filters.presentation.list

import android.content.Context
import com.f0x1d.logfox.core.di.IODispatcher
import com.f0x1d.logfox.core.tea.EffectHandler
import com.f0x1d.logfox.feature.database.model.UserFilter
import com.f0x1d.logfox.feature.filters.api.domain.ClearAllFiltersUseCase
import com.f0x1d.logfox.feature.filters.api.domain.CreateAllFiltersUseCase
import com.f0x1d.logfox.feature.filters.api.domain.DeleteFilterUseCase
import com.f0x1d.logfox.feature.filters.api.domain.GetAllFiltersFlowUseCase
import com.f0x1d.logfox.feature.filters.api.domain.SwitchFilterUseCase
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.withContext
import javax.inject.Inject

internal class FiltersEffectHandler @Inject constructor(
    @ApplicationContext private val context: Context,
    private val getAllFiltersFlowUseCase: GetAllFiltersFlowUseCase,
    private val createAllFiltersUseCase: CreateAllFiltersUseCase,
    private val switchFilterUseCase: SwitchFilterUseCase,
    private val deleteFilterUseCase: DeleteFilterUseCase,
    private val clearAllFiltersUseCase: ClearAllFiltersUseCase,
    private val gson: Gson,
    @IODispatcher private val ioDispatcher: CoroutineDispatcher,
) : EffectHandler<FiltersSideEffect, FiltersCommand> {

    override suspend fun handle(
        effect: FiltersSideEffect,
        onCommand: suspend (FiltersCommand) -> Unit,
    ) {
        when (effect) {
            is FiltersSideEffect.LoadFilters -> {
                getAllFiltersFlowUseCase()
                    .distinctUntilChanged()
                    .collect { filters ->
                        onCommand(FiltersCommand.FiltersLoaded(filters))
                    }
            }

            is FiltersSideEffect.ImportFilters -> {
                withContext(ioDispatcher) {
                    runCatching {
                        context.contentResolver.openInputStream(effect.uri)?.use { inputStream ->
                            val filters = gson.fromJson<List<UserFilter>>(
                                inputStream.readBytes().decodeToString(),
                                object : TypeToken<List<UserFilter>>() {}.type,
                            )
                            createAllFiltersUseCase(filters)
                        }
                    }
                }
            }

            is FiltersSideEffect.ExportAllFilters -> {
                withContext(ioDispatcher) {
                    runCatching {
                        context.contentResolver.openOutputStream(effect.uri)?.use { outputStream ->
                            outputStream.write(gson.toJson(effect.filters).encodeToByteArray())
                        }
                    }
                }
            }

            is FiltersSideEffect.SwitchFilter -> {
                runCatching {
                    switchFilterUseCase(effect.filter, effect.checked)
                }
            }

            is FiltersSideEffect.DeleteFilter -> {
                runCatching {
                    deleteFilterUseCase(effect.filter)
                }
            }

            is FiltersSideEffect.ClearAllFilters -> {
                runCatching {
                    clearAllFiltersUseCase()
                }
            }
        }
    }
}
