package com.f0x1d.logfox.feature.filters.impl.mapper

import com.f0x1d.logfox.feature.database.entity.UserFilterEntity
import com.f0x1d.logfox.feature.filters.api.model.UserFilter

internal fun UserFilterEntity.toDomainModel() = UserFilter(
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

internal fun UserFilter.toEntity() = UserFilterEntity(
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
