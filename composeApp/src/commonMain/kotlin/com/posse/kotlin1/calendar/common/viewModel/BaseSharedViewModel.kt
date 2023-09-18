package com.posse.kotlin1.calendar.common.viewModel

import kotlinx.coroutines.CoroutineScope
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
    protected val _viewState = MutableStateFlow(initialState)
    val viewState: StateFlow<State> = _viewState.asStateFlow()

    protected val _viewAction = MutableSharedFlow<Action>()
    val viewAction: SharedFlow<Action> = _viewAction.asSharedFlow()

    abstract fun obtainEvent(viewEvent: Event)

    protected fun withViewModelScope(block: suspend CoroutineScope.() -> Unit) {
        viewModelScope.launch(block = block)
    }
}