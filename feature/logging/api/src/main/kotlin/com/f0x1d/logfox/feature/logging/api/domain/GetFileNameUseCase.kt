package com.f0x1d.logfox.feature.logging.api.domain

import android.net.Uri

interface GetFileNameUseCase {
    operator fun invoke(uri: Uri): String?
}
