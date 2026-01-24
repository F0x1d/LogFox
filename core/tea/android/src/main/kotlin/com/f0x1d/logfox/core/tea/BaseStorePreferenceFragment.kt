package com.f0x1d.logfox.core.tea

import android.os.Bundle
import android.view.View
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.preference.PreferenceFragmentCompat
import kotlinx.coroutines.launch

/**
 * Base PreferenceFragment for TEA architecture.
 *
 * Features:
 * - Automatic state collection via [render]
 * - Automatic side effect handling via [handleSideEffect]
 * - Convenience [send] method for dispatching commands
 *
 * @param State immutable state type
 * @param Command user actions or system events that can modify state
 * @param SideEffect side effects for UI actions (navigation, toasts) or business logic
 * @param VM ViewModel type extending [BaseStoreViewModel]
 */
abstract class BaseStorePreferenceFragment<
    State,
    Command,
    SideEffect,
    VM : BaseStoreViewModel<State, Command, SideEffect>,
    > : PreferenceFragmentCompat() {

    /**
     * The ViewModel for this fragment. Should be obtained via `by viewModels()` or similar.
     */
    protected abstract val viewModel: VM

    /**
     * Render state to UI. Called on every state change.
     * Must be idempotent - same state = same UI.
     */
    abstract fun render(state: State)

    /**
     * Handle side effects (navigation, snackbars, etc.)
     * Called for ALL side effects - ignore those not relevant to UI.
     * Business logic side effects are handled by EffectHandlers.
     */
    abstract fun handleSideEffect(sideEffect: SideEffect)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch { viewModel.state.collect { state -> render(state) } }
                launch { viewModel.sideEffects.collect { effect -> handleSideEffect(effect) } }
            }
        }
    }

    /**
     * Convenience method to send commands to ViewModel.
     */
    protected fun send(command: Command) {
        viewModel.send(command)
    }
}
