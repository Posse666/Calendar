package com.posse.kotlin1.calendar.common.viewModel

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import moe.tlaster.precompose.viewmodel.ViewModel
import moe.tlaster.precompose.viewmodel.viewModelScope

abstract class BaseSharedViewModel<State : Any, Action, Event>(initialState: State) : ViewModel() {
    private val _viewStates = MutableStateFlow(initialState)
    fun viewStates(): StateFlow<State> = _viewStates.asStateFlow()

    private val _viewActions = MutableSharedFlow<Action?>(replay = 1, onBufferOverflow = BufferOverflow.DROP_OLDEST)
    fun viewActions(): SharedFlow<Action?> = _viewActions.asSharedFlow()

    protected var viewState: State
        get() = _viewStates.value
        set(value) {
            _viewStates.value = value
        }

    protected var viewAction: Action?
        get() = _viewActions.replayCache.last()
        set(value) {
            _viewActions.tryEmit(value)
        }

    abstract fun obtainEvent(viewEvent: Event)

    protected fun withViewModelScope(block: suspend CoroutineScope.() -> Unit) {
        viewModelScope.launch(block = block)
    }
}