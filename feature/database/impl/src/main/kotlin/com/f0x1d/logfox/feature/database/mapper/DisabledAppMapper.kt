package com.f0x1d.logfox.feature.database.mapper

import com.f0x1d.logfox.feature.database.entity.DisabledAppEntity
import com.f0x1d.logfox.feature.database.model.DisabledApp

internal fun DisabledAppEntity.toDomain() = DisabledApp(
    id = id,
    packageName = packageName,
)

internal fun DisabledApp.toEntity() = DisabledAppEntity(
    id = id,
    packageName = packageName,
)
