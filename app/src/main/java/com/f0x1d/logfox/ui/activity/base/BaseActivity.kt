package com.f0x1d.logfox.ui.activity.base

import android.content.Context
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.viewbinding.ViewBinding
import com.f0x1d.logfox.extensions.applyNavigationBarTheme
import com.f0x1d.logfox.extensions.context.viewPump
import com.f0x1d.logfox.extensions.views.snackbar
import dev.chrisbanes.insetter.applyInsetter
import io.github.inflationx.viewpump.ViewPumpContextWrapper

abstract class BaseActivity<T : ViewBinding>: AppCompatActivity() {

    protected lateinit var binding: T

    abstract fun inflateBinding(): T?
    protected open fun T.onCreate(savedInstanceState: Bundle?) = Unit

    override fun onCreate(savedInstanceState: Bundle?) {
        WindowCompat.setDecorFitsSystemWindows(window, false)
        window.applyNavigationBarTheme()

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

            binding.onCreate(savedInstanceState)
        }
    }

    override fun attachBaseContext(newBase: Context) {
        super.attachBaseContext(ViewPumpContextWrapper.wrap(newBase, newBase.viewPump))
    }

    protected fun snackbar(text: String) = binding.root.snackbar(text)

    protected fun snackbar(id: Int) = snackbar(getString(id))
}