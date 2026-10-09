package com.shortdrama.ai.main

import com.shortdrama.ai.core.ui.mvi.MviViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor() :
    MviViewModel<MainIntent, MainState, MainEffect>(MainState()) {

    override fun handleIntent(intent: MainIntent) {
        when (intent) {
            is MainIntent.SelectTab -> setState { copy(selectedTab = intent.tab) }
        }
    }
}
