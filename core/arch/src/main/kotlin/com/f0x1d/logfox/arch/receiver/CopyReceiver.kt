package com.f0x1d.logfox.arch.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.f0x1d.logfox.arch.copyText
import com.f0x1d.logfox.arch.notificationManagerCompat
import com.f0x1d.logfox.arch.toast
import com.f0x1d.logfox.strings.Strings

class CopyReceiver: BroadcastReceiver() {

    companion object {
        const val EXTRA_PACKAGE_NAME = "package_name"
        const val EXTRA_NOTIFICATION_ID = "notification_id"
    }

    override fun onReceive(context: Context, intent: Intent) {
        context.copyText(intent.getStringExtra(Intent.EXTRA_TEXT) ?: "")
        context.toast(Strings.text_copied)

        context.notificationManagerCompat.cancel(
            intent.getStringExtra(EXTRA_PACKAGE_NAME),
            intent.getIntExtra(EXTRA_NOTIFICATION_ID, 0)
        )
    }
}
