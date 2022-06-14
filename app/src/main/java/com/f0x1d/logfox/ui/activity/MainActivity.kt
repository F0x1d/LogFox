package com.f0x1d.logfox.ui.activity

import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.NavOptions
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.f0x1d.logfox.R
import com.f0x1d.logfox.databinding.ActivityMainBinding
import com.f0x1d.logfox.ui.activity.base.BaseViewModelActivity
import com.f0x1d.logfox.utils.event.Event
import com.f0x1d.logfox.viewmodel.MainViewModel
import com.google.android.material.shape.MaterialShapeDrawable
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity: BaseViewModelActivity<MainViewModel, ActivityMainBinding>(), NavController.OnDestinationChangedListener {

    override val viewModel by viewModels<MainViewModel>()
    private lateinit var navController: NavController

    override fun inflateBinding() = ActivityMainBinding.inflate(layoutInflater)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        navController = findNavController(R.id.nav_host_fragment_content_main)

        binding.bottomNavigation.setupWithNavController(navController)
        navController.addOnDestinationChangedListener(this)
    }

    override fun onEvent(event: Event) {
        when (event.type) {
            MainViewModel.EVENT_TYPE_SETUP -> {
                navController.navigate(R.id.setupFragment, null, NavOptions.Builder().apply {
                    if (navController.currentDestination != null) setPopUpTo(navController.currentDestination!!.id, true)
                }.build())
            }
        }
    }

    override fun onDestinationChanged(controller: NavController, destination: NavDestination, arguments: Bundle?) {
        val barShown = when (destination.id) {
            R.id.setupFragment -> false
            R.id.logsExtendedCopyFragment -> false
            else -> true
        }

        binding.bottomNavigation.visibility = if (barShown) View.VISIBLE else View.GONE
        window.navigationBarColor = if (barShown) (binding.bottomNavigation.background as MaterialShapeDrawable).resolvedTintColor else Color.TRANSPARENT
    }

    override fun onDestroy() {
        super.onDestroy()
        navController.removeOnDestinationChangedListener(this)
    }
}