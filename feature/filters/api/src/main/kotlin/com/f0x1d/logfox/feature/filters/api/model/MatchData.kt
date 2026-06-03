package com.f0x1d.logfox.feature.filters.api.model

import com.f0x1d.logfox.core.utils.GsonSkip

/**
 * A filter value paired with how it should be matched against a log line field. Reusable across any
 * string field (currently the tag), so match modes aren't hard-wired to a single field.
 *
 * Only [value] and [matchMode] participate in equals/hashCode (the [regex] is derived from them and
 * marked [GsonSkip]), so copies and DiffUtil/distinctUntilChanged comparisons stay correct.
 */
data class MatchData(
    val value: String? = null,
    val matchMode: MatchMode = MatchMode.CONTAINS,
) {

    // Compiled lazily, on first use, and cached for the instance's lifetime. Lazy (rather than an
    // eager field) is deliberate:
    //  - Correctness: Gson deserializes via the all-defaults no-arg constructor and then sets
    //    value/matchMode reflectively, so an eager field would compile from the default (null) value
    //    before those are populated and end up stale. Lazy defers until after they're set.
    //  - Performance: the filter pipeline calls matches() once per log line, so compiling once per
    //    instance instead of per call matters.
    // Null when not in regex mode, or when the pattern is invalid — the data then matches nothing
    // instead of crashing the pipeline. Excluded from equals/hashCode/copy (it's not a constructor
    // property) and from Gson via @delegate:GsonSkip.
    @delegate:GsonSkip
    val regex: Regex? by lazy(LazyThreadSafetyMode.PUBLICATION) {
        val pattern = value
        if (matchMode != MatchMode.REGEX || pattern.isNullOrEmpty()) {
            null
        } else {
            runCatching { pattern.toRegex() }.getOrNull()
        }
    }

    /** Whether [value] is a non-blank regex in [MatchMode.REGEX] that fails to compile. */
    val isInvalidRegex: Boolean
        get() = matchMode == MatchMode.REGEX && !value.isNullOrBlank() && regex == null

    fun matches(other: String): Boolean = when {
        value == null -> true
        matchMode == MatchMode.REGEX -> regex?.containsMatchIn(other) ?: false
        matchMode == MatchMode.EXACT -> other == value
        else -> other.contains(value)
    }
}
