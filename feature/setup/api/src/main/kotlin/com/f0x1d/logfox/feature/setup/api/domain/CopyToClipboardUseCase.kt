package com.f0x1d.logfox.feature.setup.api.domain

interface CopyToClipboardUseCase {
    operator fun invoke(text: String)
}
