package com.shortdrama.ai.core.ui.base

import androidx.viewbinding.ViewBinding
import com.shortdrama.ai.core.ui.extension.collectWhenStarted
import com.shortdrama.ai.core.ui.mvi.MviViewModel
import com.shortdrama.ai.core.ui.mvi.UiEffect
import com.shortdrama.ai.core.ui.mvi.UiIntent
import com.shortdrama.ai.core.ui.mvi.UiState

/**
 * Fragment side of MVI: collects [MviViewModel.state] into [render] and
 * [MviViewModel.effect] into [handleEffect], bound to the view lifecycle.
 */
abstract class BaseMviFragment<VB : ViewBinding, I : UiIntent, S : UiState, E : UiEffect>(
    inflate: Inflate<VB>,
) : BaseFragment<VB>(inflate) {

    protected abstract val viewModel: MviViewModel<I, S, E>

    override fun setupViews() {
        super.setupViews()
        collectWhenStarted(viewModel.state, ::render)
        collectWhenStarted(viewModel.effect, ::handleEffect)
    }

    protected abstract fun render(state: S)

    protected open fun handleEffect(effect: E) = Unit

    protected fun dispatch(intent: I) = viewModel.dispatch(intent)
}
