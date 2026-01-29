package com.f0x1d.logfox.feature.crashes.api.domain

import android.net.Uri
import com.f0x1d.logfox.feature.crashes.api.model.AppCrash

interface ExportCrashToZipUseCase {
    suspend operator fun invoke(uri: Uri, appCrash: AppCrash, crashLog: String?)
}
