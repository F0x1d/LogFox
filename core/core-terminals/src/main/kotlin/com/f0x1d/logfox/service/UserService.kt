package com.f0x1d.logfox.service

import android.content.Context
import android.os.ParcelFileDescriptor
import android.os.ParcelFileDescriptor.AutoCloseInputStream
import android.os.ParcelFileDescriptor.AutoCloseOutputStream
import androidx.annotation.Keep
import com.f0x1d.logfox.IUserService
import com.f0x1d.logfox.model.terminal.TerminalResult
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.io.InputStream
import kotlin.system.exitProcess

class UserService(): IUserService.Stub() {

    private var serviceScope: CoroutineScope? = null

    private var latestId = 0L
    private val currentProcesses = HashMap<Long, Process>()

    // Needed for shizuku
    @Suppress("UNUSED_PARAMETER")
    @Keep
    constructor(context: Context): this()

    init {
        serviceScope = CoroutineScope(Dispatchers.IO)
    }

    override fun destroy() {
        currentProcesses.values.forEach { process ->
            process.tryDestroy()
        }

        serviceScope?.cancel()
        serviceScope = null

        exitProcess(0)
    }

    override fun exit() {
        destroy()
    }

    override fun executeNow(command: String?) = runBlocking(Dispatchers.IO) {
        val process = Runtime.getRuntime().exec(command)

        val output = async(Dispatchers.IO) {
            process.inputStream.readBytes().decodeToString()
        }
        val error = async(Dispatchers.IO) {
            process.errorStream.readBytes().decodeToString()
        }
        val exitCode = process.waitFor()

        TerminalResult(exitCode, output.await(), error.await())
    }

    override fun execute(command: String?): Long {
        val processId = latestId++

        val process = Runtime.getRuntime().exec(command)
        currentProcesses[processId] = process

        return processId
    }

    override fun processOutput(processId: Long): ParcelFileDescriptor? {
        val process = currentProcesses[processId] ?: return null

        return pipeFrom(process.inputStream)
    }

    override fun processError(processId: Long): ParcelFileDescriptor? {
        val process = currentProcesses[processId] ?: return null

        return pipeFrom(process.errorStream)
    }

    private fun pipeFrom(inputStream: InputStream): ParcelFileDescriptor {
        val pipe = ParcelFileDescriptor.createPipe()
        serviceScope?.launch(Dispatchers.IO) {
            AutoCloseOutputStream(pipe[1]).use {
                try {
                    inputStream.copyTo(it)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }

        return pipe[0]
    }

    override fun processInput(processId: Long): ParcelFileDescriptor? {
        val process = currentProcesses[processId] ?: return null

        val pipe = ParcelFileDescriptor.createPipe()
        serviceScope?.launch(Dispatchers.IO) {
            AutoCloseInputStream(pipe[0]).use {
                try {
                    it.copyTo(process.outputStream)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }

        return pipe[1]
    }

    override fun destroyProcess(processId: Long) {
        currentProcesses.remove(processId)?.tryDestroy()
    }

    private fun Process.tryDestroy() = try {
        destroy()
    } catch (e: Exception) {
        // can't destroy, maybe already dead?
    }
}
