package com.f0x1d.logfox.feature.crashes.impl.mapper

import com.f0x1d.logfox.feature.crashes.api.model.DisabledApp
import com.f0x1d.logfox.feature.database.entity.DisabledAppEntity

internal fun DisabledAppEntity.toDomain() = DisabledApp(
    id = id,
    packageName = packageName,
)

internal fun DisabledApp.toEntity() = DisabledAppEntity(
    id = id,
    packageName = packageName,
)
