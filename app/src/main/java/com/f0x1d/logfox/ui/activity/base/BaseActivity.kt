package com.f0x1d.logfox.ui.activity.base

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.viewbinding.ViewBinding
import com.f0x1d.logfox.LogFoxApp
import com.f0x1d.logfox.R
import com.f0x1d.logfox.extensions.context.isNightMode
import com.f0x1d.logfox.extensions.context.viewPump
import com.f0x1d.logfox.extensions.contrastedNavBarAvailable
import com.f0x1d.logfox.extensions.gesturesAvailable
import com.f0x1d.logfox.extensions.isHuawei
import com.f0x1d.logfox.extensions.views.snackbar
import dev.chrisbanes.insetter.applyInsetter
import io.github.inflationx.viewpump.ViewPumpContextWrapper

abstract class BaseActivity<T : ViewBinding>: AppCompatActivity() {

    var appPreferences = LogFoxApp.instance.appPreferences

    private val isDarkNightActivity by lazy { appPreferences.blackNightTheme }

    protected lateinit var binding: T

    abstract fun inflateBinding(): T?

    override fun onCreate(savedInstanceState: Bundle?) {
        if (isNightMode && isDarkNightActivity) theme.applyStyle(R.style.ThemeOverlay_LogFox_Black,true)

        WindowCompat.setDecorFitsSystemWindows(window, false)

        if (!isHuawei) window.navigationBarColor = when {
            !contrastedNavBarAvailable -> getColor(
                R.color.transparent_black
            )
            !gesturesAvailable -> getColor(
                R.color.navbar_transparent_background
            )

            else -> Color.TRANSPARENT
        }

        super.onCreate(savedInstanceState)

        inflateBinding()?.also {
            binding = it
            setContentView(it.root)

            binding.root.applyInsetter {
                type(
                    navigationBars = true,
                    displayCutout = true
                ) {
                    padding(horizontal = true)
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        if (isNightMode && isDarkNightActivity != appPreferences.blackNightTheme)
            recreate()
    }

    override fun attachBaseContext(newBase: Context) {
        super.attachBaseContext(ViewPumpContextWrapper.wrap(newBase, newBase.viewPump))
    }

    protected fun snackbar(text: String) = binding.root.snackbar(text)

    protected fun snackbar(id: Int) = snackbar(getString(id))
}