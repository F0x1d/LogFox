package com.f0x1d.logfox.feature.crashes.impl.repository

import com.f0x1d.logfox.arch.di.IODispatcher
import com.f0x1d.logfox.database.AppDatabase
import com.f0x1d.logfox.database.entity.AppCrash
import com.f0x1d.logfox.feature.crashes.api.controller.CrashesNotificationsController
import com.f0x1d.logfox.feature.crashes.api.repository.CrashesRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext
import javax.inject.Inject

internal class CrashesRepositoryImpl @Inject constructor(
    private val notificationsController: CrashesNotificationsController,
    private val database: AppDatabase,
    @IODispatcher private val ioDispatcher: CoroutineDispatcher,
) : CrashesRepository {

    override fun getAllAsFlow(): Flow<List<AppCrash>> =
        database.appCrashes().getAllAsFlow()
            .distinctUntilChanged()
            .flowOn(ioDispatcher)

    override fun getByIdAsFlow(id: Long): Flow<AppCrash?> =
        database.appCrashes().getByIdAsFlow(id).flowOn(ioDispatcher)

    override suspend fun getAll(): List<AppCrash> = withContext(ioDispatcher) {
        database.appCrashes().getAll()
    }

    override suspend fun getById(id: Long): AppCrash? = withContext(ioDispatcher) {
        database.appCrashes().getById(id)
    }

    override suspend fun getAllByDateAndTime(
        dateAndTime: Long,
        packageName: String
    ): List<AppCrash> = withContext(ioDispatcher) {
        database.appCrashes().getAllByDateAndTime(
            dateAndTime = dateAndTime,
            packageName = packageName,
        )
    }

    override suspend fun insert(appCrash: AppCrash): Long = withContext(ioDispatcher) {
        database.appCrashes().insert(appCrash)
    }

    override suspend fun deleteAllByPackageName(appCrash: AppCrash) = withContext(ioDispatcher) {
        database.appCrashes().getAllByPackageName(appCrash.packageName).forEach {
            it.deleteAssociatedFiles()
            notificationsController.cancelCrashNotificationFor(it)
        }

        database.appCrashes().deleteByPackageName(appCrash.packageName)
    }

    override suspend fun update(item: AppCrash) = withContext(ioDispatcher) {
        database.appCrashes().update(item)
    }

    override suspend fun delete(item: AppCrash) = withContext(ioDispatcher) {
        item.deleteAssociatedFiles()
        database.appCrashes().delete(item)

        notificationsController.cancelCrashNotificationFor(item)
    }

    override suspend fun clear() = withContext(ioDispatcher) {
        getAll().forEach {
            it.deleteAssociatedFiles()
        }
        database.appCrashes().deleteAll()

        notificationsController.cancelAllCrashNotifications()
    }
}
