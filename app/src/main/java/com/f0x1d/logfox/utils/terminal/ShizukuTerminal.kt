package com.f0x1d.logfox.utils.terminal

import android.content.ComponentName
import android.content.Context
import android.content.ServiceConnection
import android.content.pm.PackageManager
import android.os.IBinder
import android.os.ParcelFileDescriptor.AutoCloseInputStream
import android.os.ParcelFileDescriptor.AutoCloseOutputStream
import com.f0x1d.logfox.BuildConfig
import com.f0x1d.logfox.IUserService
import com.f0x1d.logfox.R
import com.f0x1d.logfox.extensions.SHIZUKU_PERMISSION_REQUEST_ID
import com.f0x1d.logfox.model.terminal.TerminalProcess
import com.f0x1d.logfox.model.terminal.TerminalResult
import com.f0x1d.logfox.service.shizuku.UserService
import com.f0x1d.logfox.utils.terminal.base.Terminal
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import rikka.shizuku.Shizuku
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.coroutines.Continuation
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

@Singleton
class ShizukuTerminal @Inject constructor(
    @ApplicationContext private val context: Context
): Terminal {

    companion object {
        const val INDEX = 2
    }

    override val title = R.string.shizuku

    private var userService: IUserService? = null
    private val userServiceArgs = Shizuku.UserServiceArgs(ComponentName(context, UserService::class.java))
        .daemon(false)
        .processNameSuffix("service")
        .debuggable(BuildConfig.DEBUG)
        .version(BuildConfig.VERSION_CODE)

    override suspend fun isSupported() = suspendCoroutine {
        when {
            !Shizuku.pingBinder() -> it.resume(false)
            Shizuku.isPreV11() -> it.resume(false)

            Shizuku.checkSelfPermission() == PackageManager.PERMISSION_GRANTED -> it.resumeWithServiceBinding()
            Shizuku.shouldShowRequestPermissionRationale() -> it.resume(false)

            else -> {
                val listener = object : Shizuku.OnRequestPermissionResultListener {
                    override fun onRequestPermissionResult(requestCode: Int, grantResult: Int) {
                        if (requestCode != SHIZUKU_PERMISSION_REQUEST_ID) return

                        when (grantResult == PackageManager.PERMISSION_GRANTED) {
                            true -> it.resumeWithServiceBinding()

                            else -> it.resume(false)
                        }

                        Shizuku.removeRequestPermissionResultListener(this)
                    }
                }

                Shizuku.addRequestPermissionResultListener(listener)
                Shizuku.requestPermission(SHIZUKU_PERMISSION_REQUEST_ID)
            }
        }
    }

    private fun Continuation<Boolean>.resumeWithServiceBinding() {
        if (userService != null) {
            resume(true)
            return
        }

        var alreadyConnected = false
        val userServiceConnection = object : ServiceConnection {
            override fun onServiceConnected(componentName: ComponentName?, binder: IBinder?) {
                if (binder == null || !binder.pingBinder()) return

                userService = IUserService.Stub.asInterface(binder)

                if (alreadyConnected) return

                resume(true)
                alreadyConnected = true
            }

            override fun onServiceDisconnected(componentName: ComponentName?) {
                userService = null
            }
        }

        Shizuku.bindUserService(userServiceArgs, userServiceConnection)
    }

    override suspend fun executeNow(vararg command: String) = withContext(Dispatchers.IO) {
        userService?.executeNow(command.joinToString(" ")) ?: TerminalResult(3)
    }

    override fun execute(vararg command: String) = userService?.run {
        val processId = execute(command.joinToString(" "))

        TerminalProcess(
            AutoCloseInputStream(processOutput(processId) ?: return@run null),
            AutoCloseInputStream(processError(processId) ?: return@run null),
            AutoCloseOutputStream(processInput(processId) ?: return@run null)
        ) {
            destroyProcess(processId)
        }
    }

    override suspend fun exit() {
        Shizuku.unbindUserService(userServiceArgs, null, true)
    }
}