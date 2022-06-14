package com.f0x1d.logfox.viewmodel

import android.app.Application
import android.net.Uri
import androidx.lifecycle.viewModelScope
import com.f0x1d.logfox.database.AppCrash
import com.f0x1d.logfox.database.AppDatabase
import com.f0x1d.logfox.utils.exportLogToZip
import com.f0x1d.logfox.viewmodel.base.BaseSameFlowProxyViewModel
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class CrashDetailsViewModel @AssistedInject constructor(application: Application, database: AppDatabase, @Assisted crashId: Long): BaseSameFlowProxyViewModel<AppCrash>(
    application,
    database.appCrashDao().get(crashId)
) {
    fun zip(uri: Uri) {
        viewModelScope.launch(Dispatchers.IO) {
            data.value?.apply {
                ctx.contentResolver.openOutputStream(uri)?.exportLogToZip(ctx, this)
            }
        }
    }
}

@AssistedFactory
interface CrashDetailsViewModelAssistedFactory {
    fun create(crashId: Long): CrashDetailsViewModel
}