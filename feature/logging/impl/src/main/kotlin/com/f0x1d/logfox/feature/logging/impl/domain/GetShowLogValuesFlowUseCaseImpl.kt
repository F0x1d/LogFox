package com.f0x1d.logfox.feature.logging.impl.domain

import com.f0x1d.logfox.feature.logging.api.domain.GetShowLogValuesFlowUseCase
import com.f0x1d.logfox.feature.logging.api.model.ShowLogValues
import com.f0x1d.logfox.feature.preferences.api.data.LogsSettingsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import javax.inject.Inject

internal class GetShowLogValuesFlowUseCaseImpl @Inject constructor(
    private val logsSettingsRepository: LogsSettingsRepository,
) : GetShowLogValuesFlowUseCase {

    override fun invoke(): Flow<ShowLogValues> = combine(
        logsSettingsRepository.showLogDate(),
        logsSettingsRepository.showLogTime(),
        logsSettingsRepository.showLogUid(),
        logsSettingsRepository.showLogPid(),
        logsSettingsRepository.showLogTid(),
    ) { date, time, uid, pid, tid ->
        ShowLogValuesPart1(date, time, uid, pid, tid)
    }.combine(
        combine(
            logsSettingsRepository.showLogPackage(),
            logsSettingsRepository.showLogTag(),
            logsSettingsRepository.showLogContent(),
        ) { packageName, tag, content ->
            ShowLogValuesPart2(packageName, tag, content)
        },
    ) { part1, part2 ->
        ShowLogValues(
            date = part1.date,
            time = part1.time,
            uid = part1.uid,
            pid = part1.pid,
            tid = part1.tid,
            packageName = part2.packageName,
            tag = part2.tag,
            content = part2.content,
        )
    }

    private data class ShowLogValuesPart1(
        val date: Boolean,
        val time: Boolean,
        val uid: Boolean,
        val pid: Boolean,
        val tid: Boolean,
    )

    private data class ShowLogValuesPart2(
        val packageName: Boolean,
        val tag: Boolean,
        val content: Boolean,
    )
}
