package com.f0x1d.logfox.feature.setup.impl.domain

import android.content.Context
import com.f0x1d.logfox.feature.copy.impl.copyText
import com.f0x1d.logfox.feature.setup.api.domain.CopyToClipboardUseCase
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

internal class CopyToClipboardUseCaseImpl @Inject constructor(
    @ApplicationContext private val context: Context,
) : CopyToClipboardUseCase {

    override fun invoke(text: String) {
        context.copyText(text)
    }
}
