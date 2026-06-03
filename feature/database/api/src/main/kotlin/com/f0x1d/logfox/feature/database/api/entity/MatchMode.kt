package com.f0x1d.logfox.feature.database.api.entity

// Database-local copy of the filters-domain MatchMode, so this module stays free of the filters
// feature (mirrors CrashType). filters:impl maps between the two by name. Persisted by ordinal via
// MatchModeConverter, so the order of these entries must never change.
enum class MatchMode {
    CONTAINS,
    REGEX,
    EXACT,
}
