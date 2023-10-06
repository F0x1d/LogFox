package com.f0x1d.logfox.ui.activity

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.constraintlayout.widget.ConstraintSet
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import androidx.transition.ChangeBounds
import androidx.transition.TransitionManager
import com.f0x1d.logfox.NavGraphDirections
import com.f0x1d.logfox.R
import com.f0x1d.logfox.databinding.ActivityMainBinding
import com.f0x1d.logfox.extensions.contrastedNavBarAvailable
import com.f0x1d.logfox.extensions.gesturesAvailable
import com.f0x1d.logfox.extensions.hasNotificationsPermission
import com.f0x1d.logfox.extensions.isHorizontalOrientation
import com.f0x1d.logfox.ui.activity.base.BaseViewModelActivity
import com.f0x1d.logfox.utils.event.Event
import com.f0x1d.logfox.viewmodel.MainViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity: BaseViewModelActivity<MainViewModel, ActivityMainBinding>(), NavController.OnDestinationChangedListener {

    override val viewModel by viewModels<MainViewModel>()
    private lateinit var navController: NavController

    private val requestNotificationPermissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()) {}

    private var barShown = true
    private val barScene by lazy {
        ConstraintSet().apply {
            clone(this@MainActivity, R.layout.activity_main)
        }
    }
    private val noBarScene by lazy {
        ConstraintSet().apply {
            clone(this@MainActivity, R.layout.activity_main_no_bar)
        }
    }
    private val changeBoundsTransition by lazy {
        ChangeBounds().apply {
            duration = resources.getInteger(androidx.navigation.ui.R.integer.config_navAnimTime).toLong()
        }
    }

    override fun inflateBinding() = ActivityMainBinding.inflate(layoutInflater)

    @SuppressLint("InlinedApi")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val navHostFragment = supportFragmentManager.findFragmentById(
            R.id.nav_host_fragment_content_main
        ) as NavHostFragment
        navController = navHostFragment.navController

        binding.barView?.setupWithNavController(navController)
        binding.barView?.setOnItemReselectedListener {
            // Just do nothing
        }

        navController.addOnDestinationChangedListener(this)

        if (!hasNotificationsPermission() && !viewModel.askedNotificationsPermission) {
            MaterialAlertDialogBuilder(this)
                .setIcon(R.drawable.ic_dialog_notification_important)
                .setTitle(R.string.no_notification_permission)
                .setMessage(R.string.notification_permission_is_required)
                .setCancelable(false)
                .setPositiveButton(android.R.string.ok) { dialog, which -> requestNotificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)}
                .setNegativeButton(R.string.close, null)
                .show()

            viewModel.askedNotificationsPermission = true
        }
    }

    override fun onEvent(event: Event) {
        when (event.type) {
            MainViewModel.EVENT_TYPE_SETUP -> navController.navigate(NavGraphDirections.actionGlobalSetupFragment())
        }
    }

    override fun onDestinationChanged(controller: NavController, destination: NavDestination, arguments: Bundle?) {
        val barShown = when (destination.id) {
            R.id.setupFragment -> false
            R.id.logsExtendedCopyFragment -> false
            R.id.filtersFragment -> false
            R.id.editFilterFragment -> false
            R.id.chooseAppFragment -> false
            R.id.appCrashesFragment -> false

            else -> true
        }
        val animateBarTransition = when (destination.id) {
            R.id.setupFragment -> false

            else -> true
        }

        if (!gesturesAvailable && contrastedNavBarAvailable) {
            window.navigationBarColor = when {
                barShown && !isHorizontalOrientation -> Color.TRANSPARENT

                else -> getColor(R.color.navbar_transparent_background)
            }
        }

        if (this.barShown != barShown) {
            this.barShown = barShown

            binding.root.also {
                if (animateBarTransition) TransitionManager.beginDelayedTransition(
                    it,
                    changeBoundsTransition
                )

                val scene = when (barShown) {
                    true -> barScene

                    else -> noBarScene
                }
                scene.applyTo(it)
            }
        }
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        navController.handleDeepLink(intent)
    }

    override fun onDestroy() {
        super.onDestroy()
        navController.removeOnDestinationChangedListener(this)
    }

    private val ActivityMainBinding.barView get() = bottomNavigation ?: navigationRail
}