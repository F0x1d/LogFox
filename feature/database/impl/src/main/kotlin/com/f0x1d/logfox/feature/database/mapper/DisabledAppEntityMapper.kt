package com.f0x1d.logfox.feature.database.mapper

import com.f0x1d.logfox.feature.database.entity.DisabledAppEntity
import com.f0x1d.logfox.feature.database.entity.DisabledAppRoomEntity

internal fun DisabledAppRoomEntity.toData() = DisabledAppEntity(
    id = id,
    packageName = packageName,
)

internal fun DisabledAppEntity.toRoom() = DisabledAppRoomEntity(
    id = id,
    packageName = packageName,
)
