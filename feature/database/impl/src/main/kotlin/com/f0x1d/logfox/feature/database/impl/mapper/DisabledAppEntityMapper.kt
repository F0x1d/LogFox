package com.f0x1d.logfox.feature.database.impl.mapper

import com.f0x1d.logfox.feature.database.api.entity.DisabledAppEntity
import com.f0x1d.logfox.feature.database.impl.entity.DisabledAppRoomEntity

internal fun DisabledAppRoomEntity.toData() = DisabledAppEntity(
    id = id,
    packageName = packageName,
)

internal fun DisabledAppEntity.toRoom() = DisabledAppRoomEntity(
    id = id,
    packageName = packageName,
)
