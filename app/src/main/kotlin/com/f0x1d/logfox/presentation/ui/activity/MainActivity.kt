package com.f0x1d.logfox.presentation.ui.activity

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
import com.f0x1d.logfox.core.compat.contrastedNavBarAvailable
import com.f0x1d.logfox.core.compat.gesturesAvailable
import com.f0x1d.logfox.core.context.hasNotificationsPermission
import com.f0x1d.logfox.core.context.isHorizontalOrientation
import com.f0x1d.logfox.core.ui.base.activity.BaseActivity
import com.f0x1d.logfox.core.ui.icons.Icons
import com.f0x1d.logfox.databinding.ActivityMainBinding
import com.f0x1d.logfox.feature.strings.Strings
import com.f0x1d.logfox.navigation.Directions
import com.f0x1d.logfox.navigation.NavGraphs
import com.f0x1d.logfox.presentation.MainCommand
import com.f0x1d.logfox.presentation.MainSideEffect
import com.f0x1d.logfox.presentation.MainState
import com.f0x1d.logfox.presentation.MainViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity :
    BaseActivity<ActivityMainBinding>(),
    NavController.OnDestinationChangedListener {

    private val viewModel by viewModels<MainViewModel>()

    private val navController by lazy {
        val navHostFragment = supportFragmentManager.findFragmentById(
            R.id.nav_host_fragment_content_main,
        ) as NavHostFragment

        navHostFragment.navController
    }

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
            duration =
                resources.getInteger(androidx.navigation.ui.R.integer.config_navAnimTime).toLong()
        }
    }

    override fun inflateBinding() = ActivityMainBinding.inflate(layoutInflater)

    @SuppressLint("InlinedApi")
    override fun ActivityMainBinding.onCreate(savedInstanceState: Bundle?) {
        setupNavigation(viewModel.state.value.openCrashesOnStartup)

        barView?.setOnItemReselectedListener {
            // Just do nothing
        }
        setupBarInsets()

        showNotificationPermissionDialogIfNeeded(viewModel.state.value)

        viewModel.sideEffects.collectWithLifecycle { sideEffect ->
            when (sideEffect) {
                MainSideEffect.OpenSetup -> navController.navigate(
                    Directions.action_global_setupFragment,
                )

                else -> Unit // Handled by EffectHandler
            }
        }
    }

    private fun showNotificationPermissionDialogIfNeeded(state: MainState) {
        if (!hasNotificationsPermission() && !state.askedNotificationsPermission) {
            MaterialAlertDialogBuilder(this@MainActivity)
                .setIcon(Icons.ic_dialog_notification_important)
                .setTitle(Strings.no_notification_permission)
                .setMessage(Strings.notification_permission_is_required)
                .setCancelable(false)
                .setPositiveButton(android.R.string.ok) { _, _ ->
                    requestNotificationPermissionLauncher.launch(
                        Manifest.permission.POST_NOTIFICATIONS,
                    )
                }
                .setNegativeButton(Strings.close, null)
                .show()

            viewModel.send(MainCommand.MarkNotificationsPermissionAsked)
        }
    }

    private fun ActivityMainBinding.setupNavigation(openCrashesOnStartup: Boolean) {
        navController.graph = navController.navInflater.inflate(NavGraphs.nav_graph).apply {
            setStartDestination(
                startDestId = if (openCrashesOnStartup) {
                    Directions.crashes
                } else {
                    Directions.logs
                },
            )
        }

        barView?.setupWithNavController(navController)

        navController.addOnDestinationChangedListener(this@MainActivity)
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
        if (onNewIntent) {
            if (navController.handleDeepLink(intent)) {
                return
            }
        }

        if (intent == null) return

        if (intent.data == null) return

        when (intent.action) {
            Intent.ACTION_VIEW -> navController.navigate(
                resId = Directions.action_global_logsFragment,
                args = bundleOf("file_uri" to intent.data),
            )
        }
    }

    override fun onDestinationChanged(
        controller: NavController,
        destination: NavDestination,
        arguments: Bundle?,
    ) {
        val barShown = when (destination.id) {
            Directions.setupFragment -> false
            Directions.logsExtendedCopyFragment -> false
            Directions.filtersFragment -> false
            Directions.editFilterFragment -> false
            Directions.appsPickerFragment -> false
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

                else -> getColor(
                    com.f0x1d.logfox.core.ui.theme.R.color.navbar_transparent_background,
                )
            }
        } else if (gesturesAvailable) {
            window.isNavigationBarContrastEnforced = !(barShown && !isHorizontalOrientation)
        }

        if (this.barShown != barShown) {
            this.barShown = barShown

            binding.root.also {
                if (animateBarTransition) {
                    TransitionManager.beginDelayedTransition(
                        it,
                        changeBoundsTransition,
                    )
                }

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
                    val navigationBarsInsets = insets.getInsets(
                        WindowInsetsCompat.Type.navigationBars(),
                    )
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
