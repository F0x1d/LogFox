package com.f0x1d.logfox.ui.activity.base

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.viewbinding.ViewBinding

abstract class BaseActivity<T : ViewBinding>: AppCompatActivity() {

    protected lateinit var binding: T

    abstract fun inflateBinding(): T?

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        inflateBinding()?.also {
            binding = it
            setContentView(it.root)
        }
    }
}