package com.f0x1d.logfox.extensions.logline

import android.content.Context
import androidx.collection.LruCache
import com.f0x1d.logfox.model.LogLevel
import com.f0x1d.logfox.model.LogLine
import com.f0x1d.logfox.utils.UIDS

private val logRegex = "(.{14}) (.{1,5}) (.{1,5}) (.{1,5}) (.) (.+?): (.+)".toRegex()
// time, uid, pid, tid, level, tag, message
private val uidRegex = "u(.+?).*a(.+)".toRegex()

private val uidsCache = LruCache<String, String>(200)

fun LogLine(
    id: Long,
    line: String,
    context: Context
) = logRegex.find(line.trim())?.run {
    val uid = groupValues[2].replace(" ", "")
    val integerUid = uidRegex.find(uid)?.run {
        100_000 * groupValues[1].toInt() + 10_000 + groupValues[2].toInt()
    } ?: uid.toIntOrNull() ?: UIDS.MAPPINGS[uid]

    val packageName = uidsCache[uid] ?: integerUid?.let {
        context.packageManager.getPackagesForUid(it)?.firstOrNull()?.also { packageName ->
            uidsCache.put(uid, packageName)
        }
    }

    val time = groupValues[1].replace(" ", "").run {
        indexOf(".").let {
            substring(0, it).toLong() * 1000 + substring(it + 1).toLong()
        }
    }

    LogLine(
        id,
        time,
        uid,
        groupValues[3].replace(" ", ""),
        groupValues[4].replace(" ", ""),
        packageName,
        mapLevel(groupValues[5]),
        groupValues[6].trim(),
        groupValues[7],
        groupValues[0]
    )
}

private fun mapLevel(level: String) = LogLevel.values().find {
    it.letter == level
} ?: throw RuntimeException("wtf is $level")