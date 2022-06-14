package com.f0x1d.logfox.viewmodel.base

import android.app.Application
import kotlinx.coroutines.flow.Flow

abstract class BaseSameFlowProxyViewModel<T>(application: Application, flow: Flow<T?>): BaseFlowProxyViewModel<T, T>(application, flow) {
    override fun map(data: T?) = data
}