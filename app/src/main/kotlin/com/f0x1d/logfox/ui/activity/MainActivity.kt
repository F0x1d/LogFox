package com.f0x1d.logfox.ui.activity

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.os.bundleOf
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import androidx.transition.ChangeBounds
import androidx.transition.TransitionManager
import com.f0x1d.logfox.R
import com.f0x1d.logfox.arch.contrastedNavBarAvailable
import com.f0x1d.logfox.arch.gesturesAvailable
import com.f0x1d.logfox.arch.ui.activity.BaseViewModelActivity
import com.f0x1d.logfox.context.hasNotificationsPermission
import com.f0x1d.logfox.context.isHorizontalOrientation
import com.f0x1d.logfox.databinding.ActivityMainBinding
import com.f0x1d.logfox.model.event.Event
import com.f0x1d.logfox.navigation.Directions
import com.f0x1d.logfox.strings.Strings
import com.f0x1d.logfox.ui.Icons
import com.f0x1d.logfox.viewmodel.MainViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity: BaseViewModelActivity<MainViewModel, ActivityMainBinding>(), NavController.OnDestinationChangedListener {

    override val viewModel by viewModels<MainViewModel>()
    private lateinit var navController: NavController

    private val requestNotificationPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission(),
    ) { }

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
    override fun ActivityMainBinding.onCreate(savedInstanceState: Bundle?) {
        val navHostFragment = supportFragmentManager.findFragmentById(
            R.id.nav_host_fragment_content_main,
        ) as NavHostFragment
        navController = navHostFragment.navController

        barView?.setupWithNavController(navController)
        barView?.setOnItemReselectedListener {
            // Just do nothing
        }
        setupBarInsets()

        navController.addOnDestinationChangedListener(this@MainActivity)

        if (!hasNotificationsPermission() && !viewModel.askedNotificationsPermission) {
            MaterialAlertDialogBuilder(this@MainActivity)
                .setIcon(Icons.ic_dialog_notification_important)
                .setTitle(Strings.no_notification_permission)
                .setMessage(Strings.notification_permission_is_required)
                .setCancelable(false)
                .setPositiveButton(android.R.string.ok) { _, _ ->
                    requestNotificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                }
                .setNegativeButton(Strings.close, null)
                .show()

            viewModel.askedNotificationsPermission = true
        }
    }

    override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)
        handleIntent(intent)
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        handleIntent(intent, onNewIntent = true)
    }

    private fun handleIntent(intent: Intent?, onNewIntent: Boolean = false) {
        if (onNewIntent)
            if (navController.handleDeepLink(intent))
                return

        if (intent == null) return

        if (intent.data == null) return

        when (intent.action) {
            Intent.ACTION_VIEW -> navController.navigate(
                resId = Directions.action_global_logsFragment,
                args = bundleOf("file_uri" to intent.data),
            )
        }
    }

    override fun onEvent(event: Event) {
        when (event.type) {
            MainViewModel.EVENT_TYPE_SETUP -> navController.navigate(Directions.action_global_setupFragment)
        }
    }

    override fun onDestinationChanged(controller: NavController, destination: NavDestination, arguments: Bundle?) {
        val barShown = when (destination.id) {
            Directions.setupFragment -> false
            Directions.logsExtendedCopyFragment -> false
            Directions.filtersFragment -> false
            Directions.editFilterFragment -> false
            Directions.chooseAppFragment -> false
            Directions.appCrashesFragment -> false
            Directions.crashDetailsFragment -> false

            else -> true
        }
        val animateBarTransition = when (destination.id) {
            Directions.setupFragment -> false

            else -> true
        }

        if (!gesturesAvailable && contrastedNavBarAvailable) {
            window.navigationBarColor = when {
                barShown && !isHorizontalOrientation -> Color.TRANSPARENT

                else -> getColor(com.f0x1d.logfox.arch.R.color.navbar_transparent_background)
            }
        } else if (gesturesAvailable) {
            window.isNavigationBarContrastEnforced = !(barShown && !isHorizontalOrientation)
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

    private fun ActivityMainBinding.setupBarInsets() {
        barView?.let { view ->
            ViewCompat.setOnApplyWindowInsetsListener(view) { _, insets ->
                if (isHorizontalOrientation) {
                    val statusBarInsets = insets.getInsets(WindowInsetsCompat.Type.statusBars())

                    view.updatePadding(
                        left = statusBarInsets.left,
                        top = statusBarInsets.top,
                        right = statusBarInsets.right,
                        bottom = statusBarInsets.bottom,
                    )
                } else {
                    val navigationBarsInsets = insets.getInsets(WindowInsetsCompat.Type.navigationBars())
                    val imeInsets = insets.getInsets(WindowInsetsCompat.Type.ime())

                    // I don't want to see it above keyboard
                    val imeBottomInset = (imeInsets.bottom - view.height).coerceAtLeast(0)

                    view.updatePadding(
                        left = navigationBarsInsets.left,
                        top = navigationBarsInsets.top,
                        right = navigationBarsInsets.right,
                        bottom = navigationBarsInsets.bottom + imeBottomInset,
                    )
                }

                insets
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        navController.removeOnDestinationChangedListener(this)
    }

    private val ActivityMainBinding.barView get() = bottomNavigation ?: navigationRail
}
