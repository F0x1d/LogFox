package com.f0x1d.logfox.arch.repository

import kotlinx.coroutines.flow.Flow

interface DatabaseProxyRepository<T> {
    fun getAllAsFlow(): Flow<List<T>>
    fun getByIdAsFlow(id: Long): Flow<T?>

    suspend fun getAll(): List<T>
    suspend fun getById(id: Long): T?

    suspend fun update(item: T)
    suspend fun delete(item: T)
    suspend fun clear()
}
