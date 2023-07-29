package com.f0x1d.logfox.utils.terminal

import android.content.pm.PackageManager
import android.os.Build
import androidx.annotation.RequiresApi
import com.f0x1d.logfox.R
import com.f0x1d.logfox.extensions.SHIZUKU_PERMISSION_REQUEST_ID
import com.f0x1d.logfox.utils.terminal.base.Terminal
import com.f0x1d.logfox.utils.terminal.base.TerminalProcess
import com.f0x1d.logfox.utils.terminal.base.TerminalResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.withContext
import rikka.shizuku.Shizuku
import rikka.shizuku.ShizukuRemoteProcess
import java.lang.reflect.Method
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

@RequiresApi(23)
@Singleton
class ShizukuTerminal @Inject constructor(): Terminal {

    companion object {
        const val INDEX = 2
    }

    override val title = R.string.shizuku

    private var newProcessMethod: Method? = null

    // I know this is bad, but why would they remove newProcess? Am i supposed to copy their code?
    private fun createProcess(command: Array<out String>) = try {
        if (newProcessMethod == null) {
            newProcessMethod = Shizuku::class.java.getDeclaredMethod(
                "newProcess",
                Array<String>::class.java,
                Array<String>::class.java,
                String::class.java
            ).also { it.isAccessible = true }
        }

        newProcessMethod!!.invoke(
            null,
            arrayOf("sh", "-c", command.joinToString(" ")),
            null,
            null
        ) as ShizukuRemoteProcess
    } catch (e: Exception) {
        null
    }

    override suspend fun isSupported() = suspendCoroutine {
        if (!shizukuAvailable) it.resume(false)

        when {
            !Shizuku.pingBinder() -> it.resume(false)
            Shizuku.isPreV11() -> it.resume(false)

            Shizuku.checkSelfPermission() == PackageManager.PERMISSION_GRANTED -> it.resume(true)
            Shizuku.shouldShowRequestPermissionRationale() -> it.resume(false)

            else -> {
                val listener = object : Shizuku.OnRequestPermissionResultListener {
                    override fun onRequestPermissionResult(requestCode: Int, grantResult: Int) {
                        if (requestCode != SHIZUKU_PERMISSION_REQUEST_ID) return

                        it.resume(grantResult == PackageManager.PERMISSION_GRANTED)
                        Shizuku.removeRequestPermissionResultListener(this)
                    }
                }

                Shizuku.addRequestPermissionResultListener(listener)
                Shizuku.requestPermission(SHIZUKU_PERMISSION_REQUEST_ID)
            }
        }
    }

    override suspend fun executeNow(vararg command: String) = withContext(Dispatchers.IO) {
        if (!Shizuku.pingBinder()) {
            return@withContext TerminalResult(3)
        }

        val process = createProcess(command) ?: return@withContext TerminalResult(3)

        val output = async(Dispatchers.IO) {
            process.inputStream.readBytes().decodeToString()
        }
        val error = async(Dispatchers.IO) {
            process.errorStream.readBytes().decodeToString()
        }
        val exitCode = process.waitFor()

        TerminalResult(exitCode, output.await(), error.await())
    }

    override fun execute(vararg command: String): TerminalProcess? {
        if (!Shizuku.pingBinder()) {
            return null
        }

        return createProcess(command)?.run {
            TerminalProcess(
                inputStream,
                errorStream,
                outputStream,
                this::destroy
            )
        }
    }
}

val shizukuAvailable get() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.M