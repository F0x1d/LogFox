package com.f0x1d.logfox.feature.filters.api.model

/**
 * How a [MatchData] value is matched against a log line field.
 *
 * This domain enum is bridged to its database-layer counterpart by name (see the filters:impl
 * mapper), so its ordinals are not persisted directly.
 */
enum class MatchMode {
    /** Substring match (default). */
    CONTAINS,

    /** The value is a regular expression, matched with [Regex.containsMatchIn]. */
    REGEX,

    /** Exact string equality. */
    EXACT,
}
