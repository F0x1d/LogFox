package com.f0x1d.logfox.ui.activity.base

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.viewbinding.ViewBinding
import com.f0x1d.logfox.R
import com.f0x1d.logfox.extensions.contrastedNavBarAvailable
import com.f0x1d.logfox.extensions.gesturesAvailable
import com.f0x1d.logfox.extensions.views.snackbar
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.EntryPointAccessors
import dagger.hilt.components.SingletonComponent
import dev.chrisbanes.insetter.applyInsetter
import io.github.inflationx.viewpump.ViewPump
import io.github.inflationx.viewpump.ViewPumpContextWrapper

abstract class BaseActivity<T : ViewBinding>: AppCompatActivity() {

    protected lateinit var binding: T

    abstract fun inflateBinding(): T?

    override fun onCreate(savedInstanceState: Bundle?) {
        WindowCompat.setDecorFitsSystemWindows(window, false)

        window.navigationBarColor = when {
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

    override fun attachBaseContext(newBase: Context) {
        val viewPump = EntryPointAccessors.fromApplication(
            newBase,
            BaseActivityEntryPoint::class.java
        ).viewPump()

        super.attachBaseContext(ViewPumpContextWrapper.wrap(newBase, viewPump))
    }

    protected fun snackbar(text: String) = binding.root.snackbar(text)

    protected fun snackbar(id: Int) = snackbar(getString(id))
}

@EntryPoint
@InstallIn(SingletonComponent::class)
interface BaseActivityEntryPoint {
    fun viewPump(): ViewPump
}