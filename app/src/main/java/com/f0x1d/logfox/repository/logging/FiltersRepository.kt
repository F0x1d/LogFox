package com.f0x1d.logfox.repository.logging

import com.f0x1d.logfox.database.AppDatabase
import com.f0x1d.logfox.database.UserFilter
import com.f0x1d.logfox.extensions.updateList
import com.f0x1d.logfox.model.LogLevel
import com.f0x1d.logfox.repository.logging.base.LoggingHelperRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FiltersRepository @Inject constructor(private val database: AppDatabase): LoggingHelperRepository() {

    val filtersFlow = MutableStateFlow(emptyList<UserFilter>())

    override suspend fun setup() {
        filtersFlow.update {
            database.userFilterDao().getAll()
        }
    }

    fun create(enabledLogLevels: List<LogLevel>, pid: String, tid: String, tag: String, content: String) = create(
        UserFilter(
            enabledLogLevels,
            pid.nullIfEmpty(),
            tid.nullIfEmpty(),
            tag.nullIfEmpty(),
            content.nullIfEmpty()
        )
    )

    fun create(userFilter: UserFilter) {
        createAll(listOf(userFilter))
    }

    fun createAll(userFilters: List<UserFilter>) {
        onAppScope {
            filtersFlow.updateList {
                userFilters.forEach {
                    add(
                        it.copy(id = database.userFilterDao().insert(it))
                    )
                }
            }
        }
    }

    fun switch(userFilter: UserFilter, checked: Boolean) = update {
        userFilter.copy(enabled = checked).also {
            database.userFilterDao().update(it)
        }
    }

    fun update(userFilter: UserFilter, enabledLogLevels: List<LogLevel>, pid: String, tid: String, tag: String, content: String) = update {
        userFilter.copy(
            allowedLevels = enabledLogLevels,
            pid = pid.nullIfEmpty(),
            tid = tid.nullIfEmpty(),
            tag = tag.nullIfEmpty(),
            content = content.nullIfEmpty()
        ).also { database.userFilterDao().update(it) }
    }

    fun update(block: () -> UserFilter) {
        onAppScope {
            filtersFlow.updateList {
                val newValue = block.invoke()
                set(indexOfFirst { it.id == newValue.id }, newValue)
            }
        }
    }

    fun delete(userFilter: UserFilter) {
        onAppScope {
            filtersFlow.updateList {
                remove(userFilter)
                database.userFilterDao().delete(userFilter)
            }
        }
    }

    fun clearFilters() {
        onAppScope {
            filtersFlow.update {
                database.userFilterDao().deleteAll()
                emptyList()
            }
        }
    }

    private fun String.nullIfEmpty() = ifEmpty { null }
}