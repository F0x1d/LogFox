package com.f0x1d.logfox.terminals

import android.content.ComponentName
import android.content.Context
import android.content.ServiceConnection
import android.content.pm.PackageManager
import android.os.IBinder
import android.os.ParcelFileDescriptor.AutoCloseInputStream
import android.os.ParcelFileDescriptor.AutoCloseOutputStream
import com.f0x1d.logfox.IUserService
import com.f0x1d.logfox.arch.di.IODispatcher
import com.f0x1d.logfox.model.terminal.TerminalProcess
import com.f0x1d.logfox.model.terminal.TerminalResult
import com.f0x1d.logfox.service.UserService
import com.f0x1d.logfox.terminals.base.Terminal
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import rikka.shizuku.Shizuku
import java.io.InputStream
import java.io.OutputStream
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.coroutines.Continuation
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

@Singleton
class ShizukuTerminal @Inject constructor(
    @ApplicationContext private val context: Context,
    @IODispatcher private val ioDispatcher: CoroutineDispatcher,
): Terminal {

    companion object {
        const val INDEX = 2

        private const val SHIZUKU_PERMISSION_REQUEST_ID = 8
    }

    override val title = R.string.shizuku

    private var userService: IUserService? = null
    private var serviceConnection: ServiceConnection? = null

    private val userServiceArgs = Shizuku.UserServiceArgs(ComponentName(context, UserService::class.java))
        .daemon(false)
        .processNameSuffix("service")
        .debuggable(BuildConfig.DEBUG)
        .version(
            context.run {
                packageManager.getPackageInfo(packageName, 0).versionCode
            }
        )

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

        var resumed = false
        serviceConnection = object : ServiceConnection {
            override fun onServiceConnected(componentName: ComponentName?, binder: IBinder?) {
                if (binder == null || !binder.pingBinder()) {
                    if (resumed.not()) {
                        resume(false)
                        resumed = true
                    }
                    return
                }

                userService = IUserService.Stub.asInterface(binder)

                if (resumed.not()) {
                    resume(true)
                    resumed = true
                }
            }

            override fun onServiceDisconnected(componentName: ComponentName?) {
                userService = null
            }
        }.also {
            Shizuku.bindUserService(userServiceArgs, it)
        }
    }

    override suspend fun executeNow(vararg command: String) = withContext(ioDispatcher) {
        userService?.executeNow(command.joinToString(" ")) ?: TerminalResult(3)
    }

    override fun execute(vararg command: String) = userService?.run {
        runCatching {
            val processId = execute(command.joinToString(" "))

            TerminalProcess(
                output = AutoCloseInputStream(processOutput(processId) ?: return@run null),
                error = AutoCloseInputStream(processError(processId) ?: return@run null),
                input = AutoCloseOutputStream(processInput(processId) ?: return@run null),
            ) {
                destroyProcess(processId)
            }
        }.getOrElse {
            val emptyInputStream = object : InputStream() {
                override fun read(): Int = 0
            }
            val stubOutputStream = object : OutputStream() {
                override fun write(data: Int) = Unit
            }

            TerminalProcess(
                output = emptyInputStream,
                error = emptyInputStream,
                input = stubOutputStream,
                destroy = { },
            )
        }
    }

    override suspend fun exit() {
        runCatching {
            Shizuku.unbindUserService(userServiceArgs, serviceConnection, true)
        }
    }
}
