package extensions

import com.android.build.api.dsl.CommonExtension

fun CommonExtension<*, *, *, *, *, *>.configureRobolectric() {
    testOptions {
        animationsDisabled = true
        unitTests.isIncludeAndroidResources = true
    }
}
