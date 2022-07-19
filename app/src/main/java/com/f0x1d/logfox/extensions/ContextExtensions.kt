package com.f0x1d.logfox.extensions

import android.Manifest
import android.app.ActivityManager
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.widget.Toast
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.f0x1d.logfox.R
import com.f0x1d.logfox.repository.logging.LoggingRepository
import com.f0x1d.logfox.service.LoggingService
import kotlin.system.exitProcess


fun Context.copyText(text: String) = (getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager)
    .setPrimaryClip(ClipData.newPlainText(getString(R.string.app_name), text))
    .apply {
        toast(R.string.text_copied)
    }

fun Context.hasPermissionToReadLogs() = ContextCompat.checkSelfPermission(
    this,
    Manifest.permission.READ_LOGS
) == PackageManager.PERMISSION_GRANTED

val Context.notificationManagerCompat get() = NotificationManagerCompat.from(this)
val Context.activityManager get() = getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager

fun Context.startLoggingAndService(loggingRepository: LoggingRepository) {
    loggingRepository.startLoggingIfNot()

    Intent(this, LoggingService::class.java).also {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
            startForegroundService(it)
        else
            startService(it)
    }
}

fun Context.startLoggingAndServiceIfCan(loggingRepository: LoggingRepository) {
    if (hasPermissionToReadLogs()) {
        startLoggingAndService(loggingRepository)
    }
}

fun Context.hardRestartApp() {
    for (task in activityManager.appTasks) task.finishAndRemoveTask()

    val intent = packageManager.getLaunchIntentForPackage(packageName)
    startActivity(intent)
    exitProcess(0)
}

fun Context.toast(text: Int) = Toast.makeText(this, text, Toast.LENGTH_SHORT).show()

fun Context.shareIntent(text: String) {
    val intent = Intent(Intent.ACTION_SEND)
    intent.putExtra(Intent.EXTRA_TEXT, text)
    intent.type = "text/plain"

    startActivity(Intent.createChooser(intent, getString(R.string.share)))
}

fun Context.catchingNotNumber(block: () -> Unit) = try {
    block.invoke()
} catch (e: NumberFormatException) {
    toast(R.string.this_is_not_a_number)
}

fun Context.sendKillApp() = startService(Intent(this, LoggingService::class.java).setAction(LoggingService.ACTION_KILL_SERVICE))