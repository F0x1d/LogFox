package com.f0x1d.logfox.extensions

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import com.f0x1d.logfox.R

fun Context.isOmnibinInstalled() = try {
    packageManager.getPackageInfo("com.f0x1d.dogbin", 0)
    true
} catch (e: PackageManager.NameNotFoundException) {
    false
}

fun uploadToFoxBinIntent(content: String) = Intent("com.f0x1d.dogbin.ACTION_UPLOAD_TO_FOXBIN").apply {
    putExtra(Intent.EXTRA_TEXT, content)
    type = "text/plain"
}
fun Context.uploadToFoxBin(content: String) {
    try {
        startActivity(uploadToFoxBinIntent(content))
    } catch (e: Exception) {
        e.printStackTrace()
        toast(R.string.too_big_log)
    }
}