package com.f0x1d.logfox.feature.logging.impl.data

import android.content.Context
import androidx.collection.LruCache
import com.f0x1d.logfox.feature.logging.api.data.LogLineParser
import com.f0x1d.logfox.feature.logging.api.model.LogLevel
import com.f0x1d.logfox.feature.logging.api.model.LogLine
import dagger.hilt.android.qualifiers.ApplicationContext
import timber.log.Timber
import javax.inject.Inject

internal class LogLineParserImpl @Inject constructor(
    @ApplicationContext private val context: Context,
) : LogLineParser {

    private val logRegex = LOG_REGEX.toRegex()
    private val uidRegex = UID_REGEX.toRegex()
    private val uidsCache = LruCache<String, String>(CACHE_SIZE)

    override fun parse(id: Long, line: String): LogLine? = runCatching {
        val matchResult = logRegex.find(line.trim())

        if (matchResult == null) {
            Timber.d("Match result is null for: $line")
            return@runCatching null
        }

        val groups = matchResult.groupValues

        val uid = groups[UID_GROUP].trim()
        val packageName = resolvePackageName(uid)
        val time = parseTime(groups[TIME_GROUP])
        val level = parseLevel(groups[LEVEL_GROUP])

        LogLine(
            id = id,
            dateAndTime = time,
            uid = uid,
            pid = groups[PID_GROUP].trim(),
            tid = groups[TID_GROUP].trim(),
            packageName = packageName,
            level = level,
            tag = groups[TAG_GROUP].trim(),
            content = groups[CONTENT_GROUP],
            originalContent = groups[ORIGINAL_CONTENT_GROUP],
        )
    }.onFailure { throwable ->
        Timber.e(throwable, "Error while parsing log line")
    }.getOrNull()

    private fun resolvePackageName(uid: String): String? {
        uidsCache[uid]?.let { return it }

        val integerUid = resolveIntegerUid(uid) ?: return null

        return runCatching {
            context.packageManager
                .getPackagesForUid(integerUid)
                ?.firstOrNull()
                ?.also { packageName -> uidsCache.put(uid, packageName) }
        }.getOrNull()
    }

    private fun resolveIntegerUid(uid: String): Int? {
        uid.toIntOrNull()?.let { return it }

        WELL_KNOWN_UIDS[uid]?.let { return it }

        return uidRegex.find(uid)?.let { matchResult ->
            val userId = matchResult.groupValues[1].toInt()
            val appId = matchResult.groupValues[2].toInt()

            USER_UID_MULTIPLIER * userId + APP_UID_OFFSET + appId
        }
    }

    private fun parseTime(timeString: String): Long {
        val cleanTime = timeString.trim()
        val dotIndex = cleanTime.indexOf('.')

        val seconds = cleanTime.take(dotIndex).toLong()
        val milliseconds = cleanTime.substring(dotIndex + 1).toLong()

        return seconds * 1000 + milliseconds
    }

    private fun parseLevel(level: String): LogLevel =
        LogLevel.entries.find { it.letter == level }
            ?: throw IllegalArgumentException("Unknown log level: $level")

    private companion object {
        // Format: time, uid, pid, tid, level, tag, message
        const val LOG_REGEX = "(.{14}) (.{5,}?) (.{1,5}) (.{1,5}) (.) (.+?): (.+)"
        const val UID_REGEX = "u(.+?).*a(.+)"

        const val CACHE_SIZE = 200
        const val USER_UID_MULTIPLIER = 100_000
        const val APP_UID_OFFSET = 10_000

        // Group indices
        const val ORIGINAL_CONTENT_GROUP = 0
        const val TIME_GROUP = 1
        const val UID_GROUP = 2
        const val PID_GROUP = 3
        const val TID_GROUP = 4
        const val LEVEL_GROUP = 5
        const val TAG_GROUP = 6
        const val CONTENT_GROUP = 7
    }
}
