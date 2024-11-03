package com.f0x1d.logfox.feature.setup.presentation

import androidx.annotation.StringRes

sealed interface SetupAction {
    data class ShowSnackbar(@StringRes val textResId: Int) : SetupAction
}
