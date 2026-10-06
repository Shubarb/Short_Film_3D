package com.shortdrama.ai.core.ui.mvi

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * Base class for every screen's ViewModel.
 *
 * Unidirectional data flow:
 * View --[dispatch(intent)]--> ViewModel --[setState { }]--> state --> View.render(state)
 *                                         --[sendEffect()]--> effect --> View.handleEffect(effect)
 */
abstract class MviViewModel<I : UiIntent, S : UiState, E : UiEffect>(
    initialState: S,
) : ViewModel() {

    private val _state = MutableStateFlow(initialState)
    val state: StateFlow<S> = _state.asStateFlow()

    private val _effect = Channel<E>(Channel.BUFFERED)
    val effect: Flow<E> = _effect.receiveAsFlow()

    protected val currentState: S
        get() = _state.value

    /** The single entry point from the View. */
    fun dispatch(intent: I) {
        handleIntent(intent)
    }

    protected abstract fun handleIntent(intent: I)

    /** Reducer: produce the next state from the current one. */
    protected fun setState(reducer: S.() -> S) {
        _state.update(reducer)
    }

    protected fun sendEffect(effect: E) {
        viewModelScope.launch { _effect.send(effect) }
    }
}
