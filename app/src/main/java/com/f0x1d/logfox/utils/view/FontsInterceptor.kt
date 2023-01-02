package com.f0x1d.logfox.utils.view

import android.content.Context
import android.graphics.Typeface
import android.widget.TextView
import androidx.core.content.res.ResourcesCompat
import com.f0x1d.logfox.R
import io.github.inflationx.viewpump.InflateResult
import io.github.inflationx.viewpump.Interceptor

class FontsInterceptor(context: Context): Interceptor {

    private val boldSansTypeface = ResourcesCompat.getFont(context, R.font.google_sans_medium)
    private val fontsMap = hashMapOf(
        R.id.log_text to Typeface.MONOSPACE,
        R.id.title to boldSansTypeface,
        R.id.app_name to boldSansTypeface,
        android.R.id.title to boldSansTypeface,
        R.id.delete_button to boldSansTypeface,
        R.id.root_button to boldSansTypeface,
        R.id.adb_button to boldSansTypeface,
        R.id.search_button to boldSansTypeface,
        R.id.clear_search_button to boldSansTypeface,
        R.id.log_levels_button to boldSansTypeface,
        R.id.save_button to boldSansTypeface,
        R.id.time_text to boldSansTypeface,
        R.id.copy_text to boldSansTypeface,
        R.id.share_text to boldSansTypeface,
        R.id.foxbin_text to boldSansTypeface,
        R.id.zip_text to boldSansTypeface,
        R.id.export_text to boldSansTypeface
    )

    override fun intercept(chain: Interceptor.Chain): InflateResult {
        val result = chain.proceed(chain.request())

        result.view?.apply {
            fontsMap[id]?.also {
                if (this is TextView)
                    typeface = it
            }
        }

        return result
    }
}