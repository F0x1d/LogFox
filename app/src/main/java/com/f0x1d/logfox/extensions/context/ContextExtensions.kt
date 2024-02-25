package com.f0x1d.logfox.extensions.context

import android.Manifest
import android.annotation.SuppressLint
import android.app.ActivityManager
import android.app.NotificationManager
import android.app.UiModeManager
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.os.Build
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.core.content.getSystemService
import com.f0x1d.logfox.R
import com.f0x1d.logfox.extensions.asUri
import com.f0x1d.logfox.extensions.shouldRequestNotificationsPermission
import com.f0x1d.logfox.extensions.startForegroundServiceAvailable
import com.f0x1d.logfox.extensions.uiModeManagerAvailable
import com.f0x1d.logfox.service.LoggingService
import java.io.File
import kotlin.system.exitProcess


fun Context.copyText(text: String) = (getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager)
    .setPrimaryClip(ClipData.newPlainText(getString(R.string.app_name), text))

fun Context.hasPermissionToReadLogs() = ContextCompat.checkSelfPermission(
    this,
    Manifest.permission.READ_LOGS
) == PackageManager.PERMISSION_GRANTED

val Context.notificationManagerCompat get() = NotificationManagerCompat.from(this)
val Context.notificationManager get() = getSystemService<NotificationManager>()!!
val Context.activityManager get() = getSystemService<ActivityManager>()!!
@get:RequiresApi(Build.VERSION_CODES.S) val Context.uiModeManager get() = getSystemService<UiModeManager>()!!
val Context.inputMethodManager get() = getSystemService<InputMethodManager>()!!

fun Context.startLoggingAndService(force: Boolean = false) {
    loggingRepository.startLoggingIfNot()

    if (appPreferences.startOnLaunch || force) {
        startLoggingService()
    }
}

fun Context.startLoggingAndServiceIfCan(force: Boolean = false) {
    if (hasPermissionToReadLogs()) {
        startLoggingAndService(force)
    }
}

fun Context.startLoggingService() {
    Intent(this, LoggingService::class.java).also {
        if (startForegroundServiceAvailable)
            startForegroundService(it)
        else
            startService(it)
    }
}

fun Context.hardRestartApp() {
    for (task in activityManager.appTasks) task.finishAndRemoveTask()

    val intent = packageManager.getLaunchIntentForPackage(packageName)
    startActivity(intent)
    exitProcess(0)
}

fun Context.toast(text: Int) = Toast.makeText(this, text, Toast.LENGTH_SHORT).show()

fun Context.shareIntent(text: String) = baseShareIntent {
    it.putExtra(Intent.EXTRA_TEXT, text)
    it.type = "text/plain"
}

fun Context.shareFileIntent(file: File) = baseShareIntent {
    val uri = file.asUri(this)

    it.putExtra(Intent.EXTRA_STREAM, uri)
    it.type = "text/plain"
}

private fun Context.baseShareIntent(block: (Intent) -> Unit) {
    try {
        val intent = Intent(Intent.ACTION_SEND)
        block(intent)

        startActivity(Intent.createChooser(intent, getString(R.string.share)))
    } catch (e: Exception) {
        e.printStackTrace()
        toast(R.string.too_big_log)
    }
}

fun Context.catchingNotNumber(block: () -> Unit) = try {
    block()
} catch (e: NumberFormatException) {
    toast(R.string.this_is_not_a_number)
}

fun Context.sendKillApp() = sendService(LoggingService.ACTION_KILL_SERVICE)
fun Context.sendStopService() = sendService(LoggingService.ACTION_STOP_SERVICE)
private fun Context.sendService(action: String) = startService(Intent(this, LoggingService::class.java).setAction(action))

@SuppressLint("InlinedApi")
fun Context.hasNotificationsPermission() = if (shouldRequestNotificationsPermission)
    ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED
else
    true

fun Context.doIfNotificationsAllowed(block: NotificationManagerCompat.() -> Unit) = if (hasNotificationsPermission())
    block(notificationManagerCompat)
else
    Unit

fun Context.applyTheme(force: Boolean = false) {
    val nightMode = appPreferences.nightTheme

    if (uiModeManagerAvailable) {
        if (force) uiModeManager.setApplicationNightMode(
            if (nightMode != 0) nightMode else UiModeManager.MODE_NIGHT_CUSTOM
        )
    } else
        AppCompatDelegate.setDefaultNightMode(if (nightMode != 0) nightMode else AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
}

val Context.isHorizontalOrientation get() = resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE