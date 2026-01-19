package com.f0x1d.logfox.core.tea

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.viewbinding.ViewBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.coroutines.launch

/**
 * Base BottomSheetDialogFragment for TEA architecture with ViewBinding support.
 *
 * Features:
 * - Automatic state collection via [render]
 * - Automatic side effect handling via [handleSideEffect]
 * - ViewBinding lifecycle management
 * - Convenience [send] method for dispatching commands
 *
 * @param VB ViewBinding type for this fragment
 * @param State immutable state type
 * @param Command user actions or system events that can modify state
 * @param SideEffect side effects for UI actions (navigation, toasts) or business logic
 * @param VM ViewModel type extending [BaseStoreViewModel]
 */
abstract class BaseStoreBottomSheetFragment<
    VB : ViewBinding,
    State,
    Command,
    SideEffect,
    VM : BaseStoreViewModel<State, Command, SideEffect>,
    > : BottomSheetDialogFragment() {

    private var _binding: VB? = null
    protected val binding: VB get() = _binding!!

    /**
     * The ViewModel for this fragment. Should be obtained via `by viewModels()` or similar.
     */
    protected abstract val viewModel: VM

    /**
     * Create the ViewBinding for this fragment.
     */
    abstract fun inflateBinding(inflater: LayoutInflater, container: ViewGroup?): VB

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

    /**
     * Called after view is created but before state collection starts.
     * Override to set up views, click listeners, etc.
     */
    protected open fun VB.onViewCreated(view: View, savedInstanceState: Bundle?) = Unit

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = inflateBinding(inflater, container)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.onViewCreated(view, savedInstanceState)

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch { viewModel.state.collect { state -> render(state) } }
                launch { viewModel.sideEffects.collect { effect -> handleSideEffect(effect) } }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    /**
     * Convenience method to send commands to ViewModel.
     */
    protected fun send(command: Command) {
        viewModel.send(command)
    }
}
