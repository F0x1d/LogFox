package com.f0x1d.logfox.feature.database.impl.mapper

import com.f0x1d.logfox.feature.database.api.entity.UserFilterEntity
import com.f0x1d.logfox.feature.database.impl.entity.UserFilterRoomEntity

internal fun UserFilterRoomEntity.toData() = UserFilterEntity(
    id = id,
    including = including,
    allowedLevels = allowedLevels,
    uid = uid,
    pid = pid,
    tid = tid,
    packageName = packageName,
    tag = tag,
    content = content,
    enabled = enabled,
)

internal fun UserFilterEntity.toRoom() = UserFilterRoomEntity(
    id = id,
    including = including,
    allowedLevels = allowedLevels,
    uid = uid,
    pid = pid,
    tid = tid,
    packageName = packageName,
    tag = tag,
    content = content,
    enabled = enabled,
)
