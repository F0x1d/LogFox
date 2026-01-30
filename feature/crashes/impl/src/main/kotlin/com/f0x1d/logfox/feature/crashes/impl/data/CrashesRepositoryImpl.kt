package com.f0x1d.logfox.feature.crashes.impl.data

import com.f0x1d.logfox.core.di.IODispatcher
import com.f0x1d.logfox.feature.crashes.api.data.CrashesRepository
import com.f0x1d.logfox.feature.crashes.api.model.AppCrash
import com.f0x1d.logfox.feature.crashes.impl.mapper.toDomainModel
import com.f0x1d.logfox.feature.crashes.impl.mapper.toEntity
import com.f0x1d.logfox.feature.database.data.AppCrashDataSource
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import javax.inject.Inject

internal class CrashesRepositoryImpl @Inject constructor(
    private val notificationsLocalDataSource: CrashesNotificationsLocalDataSource,
    private val appCrashDataSource: AppCrashDataSource,
    @IODispatcher private val ioDispatcher: CoroutineDispatcher,
) : CrashesRepository {

    override fun getAllAsFlow(): Flow<List<AppCrash>> = appCrashDataSource.getAllAsFlow()
        .map { list -> list.map { it.toDomainModel() } }
        .distinctUntilChanged()
        .flowOn(ioDispatcher)

    override fun getByIdAsFlow(id: Long): Flow<AppCrash?> = appCrashDataSource.getByIdAsFlow(id)
        .map { it?.toDomainModel() }
        .flowOn(ioDispatcher)

    override suspend fun getAll(): List<AppCrash> = withContext(ioDispatcher) {
        appCrashDataSource.getAll().map { it.toDomainModel() }
    }

    override suspend fun getById(id: Long): AppCrash? = withContext(ioDispatcher) {
        appCrashDataSource.getById(id)?.toDomainModel()
    }

    override suspend fun getAllByDateAndTime(
        dateAndTime: Long,
        packageName: String,
    ): List<AppCrash> = withContext(ioDispatcher) {
        appCrashDataSource.getAllByDateAndTime(
            dateAndTime = dateAndTime,
            packageName = packageName,
        ).map { it.toDomainModel() }
    }

    override suspend fun insert(appCrash: AppCrash): Long = withContext(ioDispatcher) {
        appCrashDataSource.insert(appCrash.toEntity())
    }

    override suspend fun deleteAllByPackageName(packageName: String) = withContext(ioDispatcher) {
        appCrashDataSource.getAllByPackageName(packageName).forEach {
            it.toDomainModel().deleteAssociatedFiles()
            notificationsLocalDataSource.cancelCrashNotificationFor(it.toDomainModel())
        }

        appCrashDataSource.deleteByPackageName(packageName)
    }

    override suspend fun update(item: AppCrash) = withContext(ioDispatcher) {
        appCrashDataSource.update(item.toEntity())
    }

    override suspend fun delete(item: AppCrash) = withContext(ioDispatcher) {
        item.deleteAssociatedFiles()
        appCrashDataSource.delete(item.toEntity())

        notificationsLocalDataSource.cancelCrashNotificationFor(item)
    }

    override suspend fun clear() = withContext(ioDispatcher) {
        getAll().forEach {
            it.deleteAssociatedFiles()
        }
        appCrashDataSource.deleteAll()

        notificationsLocalDataSource.cancelAllCrashNotifications()
    }
}
