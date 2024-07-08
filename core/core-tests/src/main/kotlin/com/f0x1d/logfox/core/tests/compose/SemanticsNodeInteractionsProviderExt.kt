package com.f0x1d.logfox.core.tests.compose

import androidx.compose.ui.test.SemanticsNodeInteractionsProvider
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.performClick

fun SemanticsNodeInteractionsProvider.onNode(testTag: String) = onNode(hasTestTag(testTag))
fun SemanticsNodeInteractionsProvider.clickOn(testTag: String) = onNode(testTag).performClick()
