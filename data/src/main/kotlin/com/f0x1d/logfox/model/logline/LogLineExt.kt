package com.f0x1d.logfox.model.logline

import android.content.Context
import androidx.collection.LruCache
import com.f0x1d.logfox.model.UIDS_MAPPINGS

private val logRegex = "(.{14}) (.{5,}?) (.{1,5}) (.{1,5}) (.) (.+?): (.+)".toRegex()
// time, uid, pid, tid, level, tag, message
private val uidRegex = "u(.+?).*a(.+)".toRegex()

private val uidsCache = LruCache<String, String>(200)

fun LogLine(
    id: Long,
    line: String,
    context: Context
) = runCatching {
    logRegex.find(line.trim())?.run {
        val uid = groupValues[2].replace(" ", "")
        val integerUid = uid.toIntOrNull() ?: UIDS_MAPPINGS[uid] ?: uidRegex.find(uid)?.run {
            100_000 * groupValues[1].toInt() + 10_000 + groupValues[2].toInt()
        }

        val packageName = uidsCache[uid] ?: integerUid?.let {
            runCatching {
                context.packageManager.getPackagesForUid(it)?.firstOrNull()?.also { packageName ->
                    uidsCache.put(uid, packageName)
                }
            }.getOrNull()
        }

        val time = groupValues[1].replace(" ", "").run {
            indexOf(".").let {
                substring(0, it).toLong() * 1000 + substring(it + 1).toLong()
            }
        }

        LogLine(
            id = id,
            dateAndTime = time,
            uid = uid,
            pid = groupValues[3].replace(" ", ""),
            tid = groupValues[4].replace(" ", ""),
            packageName = packageName,
            level = mapLevel(groupValues[5]),
            tag = groupValues[6].trim(),
            content = groupValues[7],
            originalContent = groupValues[0],
        )
    }
}.getOrNull()

private fun mapLevel(level: String) = LogLevel.entries.find {
    it.letter == level
} ?: throw RuntimeException("wtf is $level")
