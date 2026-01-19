package com.f0x1d.logfox.feature.recordings.impl.di

import com.f0x1d.logfox.feature.recordings.api.domain.ClearAllRecordingsUseCase
import com.f0x1d.logfox.feature.recordings.api.domain.CreateRecordingFromLinesUseCase
import com.f0x1d.logfox.feature.recordings.api.domain.DeleteRecordingUseCase
import com.f0x1d.logfox.feature.recordings.api.domain.EndRecordingUseCase
import com.f0x1d.logfox.feature.recordings.api.domain.GetAllRecordingsFlowUseCase
import com.f0x1d.logfox.feature.recordings.api.domain.GetRecordingByIdFlowUseCase
import com.f0x1d.logfox.feature.recordings.api.domain.GetRecordingStateFlowUseCase
import com.f0x1d.logfox.feature.recordings.api.domain.NotifyLoggingStoppedUseCase
import com.f0x1d.logfox.feature.recordings.api.domain.PauseRecordingUseCase
import com.f0x1d.logfox.feature.recordings.api.domain.ProcessLogLineRecordingUseCase
import com.f0x1d.logfox.feature.recordings.api.domain.ResumeRecordingUseCase
import com.f0x1d.logfox.feature.recordings.api.domain.SaveAllRecordingsUseCase
import com.f0x1d.logfox.feature.recordings.api.domain.StartRecordingUseCase
import com.f0x1d.logfox.feature.recordings.api.domain.UpdateRecordingTitleUseCase
import com.f0x1d.logfox.feature.recordings.impl.domain.ClearAllRecordingsUseCaseImpl
import com.f0x1d.logfox.feature.recordings.impl.domain.CreateRecordingFromLinesUseCaseImpl
import com.f0x1d.logfox.feature.recordings.impl.domain.DeleteRecordingUseCaseImpl
import com.f0x1d.logfox.feature.recordings.impl.domain.EndRecordingUseCaseImpl
import com.f0x1d.logfox.feature.recordings.impl.domain.GetAllRecordingsFlowUseCaseImpl
import com.f0x1d.logfox.feature.recordings.impl.domain.GetRecordingByIdFlowUseCaseImpl
import com.f0x1d.logfox.feature.recordings.impl.domain.GetRecordingStateFlowUseCaseImpl
import com.f0x1d.logfox.feature.recordings.impl.domain.NotifyLoggingStoppedUseCaseImpl
import com.f0x1d.logfox.feature.recordings.impl.domain.PauseRecordingUseCaseImpl
import com.f0x1d.logfox.feature.recordings.impl.domain.ProcessLogLineRecordingUseCaseImpl
import com.f0x1d.logfox.feature.recordings.impl.domain.ResumeRecordingUseCaseImpl
import com.f0x1d.logfox.feature.recordings.impl.domain.SaveAllRecordingsUseCaseImpl
import com.f0x1d.logfox.feature.recordings.impl.domain.StartRecordingUseCaseImpl
import com.f0x1d.logfox.feature.recordings.impl.domain.UpdateRecordingTitleUseCaseImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
internal interface RecordingsUseCaseModule {

    @Binds
    fun bindGetAllRecordingsFlowUseCase(
        impl: GetAllRecordingsFlowUseCaseImpl,
    ): GetAllRecordingsFlowUseCase

    @Binds
    fun bindGetRecordingByIdFlowUseCase(
        impl: GetRecordingByIdFlowUseCaseImpl,
    ): GetRecordingByIdFlowUseCase

    @Binds
    fun bindDeleteRecordingUseCase(
        impl: DeleteRecordingUseCaseImpl,
    ): DeleteRecordingUseCase

    @Binds
    fun bindClearAllRecordingsUseCase(
        impl: ClearAllRecordingsUseCaseImpl,
    ): ClearAllRecordingsUseCase

    @Binds
    fun bindSaveAllRecordingsUseCase(
        impl: SaveAllRecordingsUseCaseImpl,
    ): SaveAllRecordingsUseCase

    @Binds
    fun bindUpdateRecordingTitleUseCase(
        impl: UpdateRecordingTitleUseCaseImpl,
    ): UpdateRecordingTitleUseCase

    @Binds
    fun bindCreateRecordingFromLinesUseCase(
        impl: CreateRecordingFromLinesUseCaseImpl,
    ): CreateRecordingFromLinesUseCase

    @Binds
    fun bindProcessLogLineRecordingUseCase(
        impl: ProcessLogLineRecordingUseCaseImpl,
    ): ProcessLogLineRecordingUseCase

    @Binds
    fun bindNotifyLoggingStoppedUseCase(
        impl: NotifyLoggingStoppedUseCaseImpl,
    ): NotifyLoggingStoppedUseCase

    @Binds
    fun bindStartRecordingUseCase(
        impl: StartRecordingUseCaseImpl,
    ): StartRecordingUseCase

    @Binds
    fun bindPauseRecordingUseCase(
        impl: PauseRecordingUseCaseImpl,
    ): PauseRecordingUseCase

    @Binds
    fun bindResumeRecordingUseCase(
        impl: ResumeRecordingUseCaseImpl,
    ): ResumeRecordingUseCase

    @Binds
    fun bindEndRecordingUseCase(
        impl: EndRecordingUseCaseImpl,
    ): EndRecordingUseCase

    @Binds
    fun bindGetRecordingStateFlowUseCase(
        impl: GetRecordingStateFlowUseCaseImpl,
    ): GetRecordingStateFlowUseCase
}
