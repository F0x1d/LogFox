package com.f0x1d.logfox.feature.crashes.impl.domain

import com.f0x1d.logfox.feature.crashes.api.data.CrashesRepository
import com.f0x1d.logfox.feature.crashes.api.domain.DeleteAllCrashesByPackageNameUseCase
import javax.inject.Inject

internal class DeleteAllCrashesByPackageNameUseCaseImpl @Inject constructor(
    private val crashesRepository: CrashesRepository,
) : DeleteAllCrashesByPackageNameUseCase {
    override suspend fun invoke(packageName: String) = crashesRepository.deleteAllByPackageName(packageName)
}
