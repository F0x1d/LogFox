package com.f0x1d.logfox.core.presentation.ui.activity

import android.content.Context
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.viewbinding.ViewBinding
import com.f0x1d.logfox.core.presentation.ext.enableEdgeToEdge
import com.f0x1d.logfox.core.presentation.ext.snackbar
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.EntryPointAccessors
import dagger.hilt.components.SingletonComponent
import dev.chrisbanes.insetter.applyInsetter
import io.github.inflationx.viewpump.ViewPump
import io.github.inflationx.viewpump.ViewPumpContextWrapper
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.launch

abstract class BaseActivity<T : ViewBinding> : AppCompatActivity() {

    protected lateinit var binding: T

    abstract fun inflateBinding(): T?
    protected open fun T.onCreate(savedInstanceState: Bundle?) = Unit

    override fun onCreate(savedInstanceState: Bundle?) {
        window.enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        inflateBinding()?.also {
            binding = it
            setContentView(it.root)

            binding.root.applyInsetter {
                type(
                    navigationBars = true,
                    displayCutout = true,
                ) {
                    padding(horizontal = true)
                }
            }

            binding.onCreate(savedInstanceState)
        }
    }

    override fun attachBaseContext(newBase: Context) {
        val entryPoint = EntryPointAccessors.fromApplication<ViewPumpEntryPoint>(newBase)

        super.attachBaseContext(ViewPumpContextWrapper.wrap(newBase, entryPoint.viewPump))
    }

    protected fun <T> Flow<T>.collectWithLifecycle(
        state: Lifecycle.State = Lifecycle.State.STARTED,
        collector: FlowCollector<T>,
    ) {
        lifecycleScope.launch {
            repeatOnLifecycle(state) {
                collect(collector)
            }
        }
    }

    protected fun snackbar(text: String) = binding.root.snackbar(text).apply {
        view.applyInsetter {
            type(navigationBars = true) {
                margin(vertical = true)
            }
        }
    }

    protected fun snackbar(id: Int) = snackbar(getString(id))

    @EntryPoint
    @InstallIn(SingletonComponent::class)
    internal interface ViewPumpEntryPoint {
        val viewPump: ViewPump
    }
}
