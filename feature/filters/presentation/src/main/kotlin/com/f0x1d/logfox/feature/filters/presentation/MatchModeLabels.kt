package com.f0x1d.logfox.feature.filters.presentation

import androidx.annotation.StringRes
import com.f0x1d.logfox.feature.filters.api.model.MatchMode
import com.f0x1d.logfox.feature.strings.Strings

// Single source for the user-facing label of each match mode, shared by the edit dialog and the
// filter list. The mapping can't live on the MatchMode enum itself because that's in filters:api,
// which doesn't depend on the strings module.
@StringRes
internal fun MatchMode.labelRes(): Int = when (this) {
    MatchMode.CONTAINS -> Strings.match_mode_contains
    MatchMode.REGEX -> Strings.match_mode_regex
    MatchMode.EXACT -> Strings.match_mode_exact
}
