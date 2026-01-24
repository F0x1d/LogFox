package com.f0x1d.logfox.feature.logging.impl.domain

import android.net.Uri
import com.f0x1d.logfox.feature.logging.api.domain.GetFileNameUseCase
import com.f0x1d.logfox.feature.logging.impl.data.LogFileDataSource
import javax.inject.Inject

internal class GetFileNameUseCaseImpl @Inject constructor(
    private val logFileDataSource: LogFileDataSource,
) : GetFileNameUseCase {

    override fun invoke(uri: Uri): String? = logFileDataSource.getFileName(uri)
}
