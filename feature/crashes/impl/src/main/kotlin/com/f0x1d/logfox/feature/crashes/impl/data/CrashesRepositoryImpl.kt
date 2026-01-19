package com.f0x1d.logfox.feature.crashes.impl.data

import com.f0x1d.logfox.core.di.IODispatcher
import com.f0x1d.logfox.feature.crashes.api.data.CrashesRepository
import com.f0x1d.logfox.feature.database.data.AppCrashRepository
import com.f0x1d.logfox.feature.database.model.AppCrash
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext
import javax.inject.Inject

internal class CrashesRepositoryImpl @Inject constructor(
    private val notificationsLocalDataSource: CrashesNotificationsLocalDataSource,
    private val appCrashRepository: AppCrashRepository,
    @IODispatcher private val ioDispatcher: CoroutineDispatcher,
) : CrashesRepository {

    override fun getAllAsFlow(): Flow<List<AppCrash>> = appCrashRepository.getAllAsFlow()
        .distinctUntilChanged()
        .flowOn(ioDispatcher)

    override fun getByIdAsFlow(id: Long): Flow<AppCrash?> = appCrashRepository.getByIdAsFlow(id).flowOn(ioDispatcher)

    override suspend fun getAll(): List<AppCrash> = withContext(ioDispatcher) {
        appCrashRepository.getAll()
    }

    override suspend fun getById(id: Long): AppCrash? = withContext(ioDispatcher) {
        appCrashRepository.getById(id)
    }

    override suspend fun getAllByDateAndTime(
        dateAndTime: Long,
        packageName: String,
    ): List<AppCrash> = withContext(ioDispatcher) {
        appCrashRepository.getAllByDateAndTime(
            dateAndTime = dateAndTime,
            packageName = packageName,
        )
    }

    override suspend fun insert(appCrash: AppCrash): Long = withContext(ioDispatcher) {
        appCrashRepository.insert(appCrash)
    }

    override suspend fun deleteAllByPackageName(appCrash: AppCrash) = withContext(ioDispatcher) {
        appCrashRepository.getAllByPackageName(appCrash.packageName).forEach {
            it.deleteAssociatedFiles()
            notificationsLocalDataSource.cancelCrashNotificationFor(it)
        }

        appCrashRepository.deleteByPackageName(appCrash.packageName)
    }

    override suspend fun update(item: AppCrash) = withContext(ioDispatcher) {
        appCrashRepository.update(item)
    }

    override suspend fun delete(item: AppCrash) = withContext(ioDispatcher) {
        item.deleteAssociatedFiles()
        appCrashRepository.delete(item)

        notificationsLocalDataSource.cancelCrashNotificationFor(item)
    }

    override suspend fun clear() = withContext(ioDispatcher) {
        getAll().forEach {
            it.deleteAssociatedFiles()
        }
        appCrashRepository.deleteAll()

        notificationsLocalDataSource.cancelAllCrashNotifications()
    }
}
