package com.f0x1d.logfox.feature.filters.impl.mapper

import com.f0x1d.logfox.feature.database.api.entity.UserFilterEntity
import com.f0x1d.logfox.feature.filters.api.model.MatchData
import com.f0x1d.logfox.feature.filters.api.model.MatchMode
import com.f0x1d.logfox.feature.filters.api.model.UserFilter
import com.f0x1d.logfox.feature.database.api.entity.MatchMode as EntityMatchMode

internal fun UserFilterEntity.toDomainModel() = UserFilter(
    id = id,
    name = name,
    including = including,
    allowedLevels = allowedLevels,
    uid = uid,
    pid = pid,
    tid = tid,
    packageName = packageName,
    tag = MatchData(value = tag, matchMode = tagMatchMode.toDomainModel()),
    content = MatchData(value = content, matchMode = contentMatchMode.toDomainModel()),
    enabled = enabled,
)

internal fun UserFilter.toEntity() = UserFilterEntity(
    id = id,
    name = name,
    including = including,
    allowedLevels = allowedLevels,
    uid = uid,
    pid = pid,
    tid = tid,
    packageName = packageName,
    tag = tag.value,
    tagMatchMode = tag.matchMode.toEntity(),
    content = content.value,
    contentMatchMode = content.matchMode.toEntity(),
    enabled = enabled,
)

private fun EntityMatchMode.toDomainModel(): MatchMode = when (this) {
    EntityMatchMode.CONTAINS -> MatchMode.CONTAINS
    EntityMatchMode.REGEX -> MatchMode.REGEX
    EntityMatchMode.EXACT -> MatchMode.EXACT
}

private fun MatchMode.toEntity(): EntityMatchMode = when (this) {
    MatchMode.CONTAINS -> EntityMatchMode.CONTAINS
    MatchMode.REGEX -> EntityMatchMode.REGEX
    MatchMode.EXACT -> EntityMatchMode.EXACT
}
