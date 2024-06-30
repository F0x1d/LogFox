package com.f0x1d.logfox.context

import android.Manifest
import android.annotation.SuppressLint
import android.app.ActivityManager
import android.app.NotificationManager
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.core.content.getSystemService
import com.f0x1d.logfox.arch.shouldRequestNotificationsPermission
import com.f0x1d.logfox.strings.Strings
import java.io.File
import kotlin.system.exitProcess


fun Context.copyText(text: String) = runCatching {
    (getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager)
        .setPrimaryClip(ClipData.newPlainText("LogFox", text))
}.onFailure {
    toast(Strings.error)
}

val Context.hasPermissionToReadLogs: Boolean get() = ContextCompat.checkSelfPermission(
    this,
    Manifest.permission.READ_LOGS
) == PackageManager.PERMISSION_GRANTED

val Context.notificationManagerCompat get() = NotificationManagerCompat.from(this)
val Context.notificationManager get() = getSystemService<NotificationManager>()!!
val Context.activityManager get() = getSystemService<ActivityManager>()!!
val Context.inputMethodManager get() = getSystemService<InputMethodManager>()!!

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

        startActivity(Intent.createChooser(intent, getString(Strings.share)))
    } catch (e: Exception) {
        e.printStackTrace()
        toast(Strings.too_big_log)
    }
}

fun Context.catchingNotNumber(block: () -> Unit) = try {
    block()
} catch (e: NumberFormatException) {
    toast(Strings.this_is_not_a_number)
}

inline fun <reified T> Context.sendService(
    action: String,
) = startService(Intent(this, T::class.java).setAction(action))

@SuppressLint("InlinedApi")
fun Context.hasNotificationsPermission() = if (shouldRequestNotificationsPermission)
    ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED
else
    true

fun Context.doIfNotificationsAllowed(block: NotificationManagerCompat.() -> Unit) = if (hasNotificationsPermission())
    block(notificationManagerCompat)
else
    Unit


val Context.isHorizontalOrientation get() = resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE
