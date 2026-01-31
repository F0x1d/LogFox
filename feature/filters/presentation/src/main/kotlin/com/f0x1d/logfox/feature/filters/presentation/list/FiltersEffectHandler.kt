package com.f0x1d.logfox.feature.filters.presentation.list

import com.f0x1d.logfox.core.tea.EffectHandler
import com.f0x1d.logfox.feature.filters.api.domain.ClearAllFiltersUseCase
import com.f0x1d.logfox.feature.filters.api.domain.DeleteFilterUseCase
import com.f0x1d.logfox.feature.filters.api.domain.ExportFiltersToUriUseCase
import com.f0x1d.logfox.feature.filters.api.domain.GetAllFiltersFlowUseCase
import com.f0x1d.logfox.feature.filters.api.domain.ImportFiltersFromUriUseCase
import com.f0x1d.logfox.feature.filters.api.domain.SwitchFilterUseCase
import kotlinx.coroutines.flow.distinctUntilChanged
import javax.inject.Inject

internal class FiltersEffectHandler @Inject constructor(
    private val getAllFiltersFlowUseCase: GetAllFiltersFlowUseCase,
    private val switchFilterUseCase: SwitchFilterUseCase,
    private val deleteFilterUseCase: DeleteFilterUseCase,
    private val clearAllFiltersUseCase: ClearAllFiltersUseCase,
    private val exportFiltersToUriUseCase: ExportFiltersToUriUseCase,
    private val importFiltersFromUriUseCase: ImportFiltersFromUriUseCase,
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

            is FiltersSideEffect.ImportFilters -> importFiltersFromUriUseCase(effect.uri)

            is FiltersSideEffect.ExportAllFilters -> exportFiltersToUriUseCase(effect.uri, effect.filters)

            is FiltersSideEffect.SwitchFilter -> switchFilterUseCase(effect.filter, effect.checked)

            is FiltersSideEffect.DeleteFilter -> deleteFilterUseCase(effect.filter)

            is FiltersSideEffect.ClearAllFilters -> clearAllFiltersUseCase()

            // UI side effects - handled by Fragment
            is FiltersSideEffect.NavigateToEditFilter -> Unit
            is FiltersSideEffect.NavigateToCreateFilter -> Unit
        }
    }
}
