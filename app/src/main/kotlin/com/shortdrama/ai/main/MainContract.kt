package com.shortdrama.ai.main

import com.shortdrama.ai.core.ui.mvi.UiEffect
import com.shortdrama.ai.core.ui.mvi.UiIntent
import com.shortdrama.ai.core.ui.mvi.UiState

/** Bottom navigation tabs, in display order. */
enum class MainTab { HOME, SHORT, LIBRARY, SETTING }

sealed interface MainIntent : UiIntent {
    data class SelectTab(val tab: MainTab) : MainIntent
}

data class MainState(
    val selectedTab: MainTab = MainTab.HOME,
) : UiState

sealed interface MainEffect : UiEffect
