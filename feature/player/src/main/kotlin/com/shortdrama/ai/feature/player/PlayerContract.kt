package com.shortdrama.ai.feature.player

import com.shortdrama.ai.core.model.Film
import com.shortdrama.ai.core.ui.mvi.UiEffect
import com.shortdrama.ai.core.ui.mvi.UiIntent
import com.shortdrama.ai.core.ui.mvi.UiState

sealed interface PlayerIntent : UiIntent {
    /** Sent when the player is released (background, rotation) so playback resumes where it left off. */
    data class SavePlayback(val positionMs: Long, val playWhenReady: Boolean) : PlayerIntent
    data object PlaybackFailed : PlayerIntent
    data object Retry : PlayerIntent
}

data class PlayerState(
    val isLoading: Boolean = true,
    val film: Film? = null,
    val isNotFound: Boolean = false,
    val hasPlaybackError: Boolean = false,
    val playbackPositionMs: Long = 0L,
    val playWhenReady: Boolean = true,
) : UiState

sealed interface PlayerEffect : UiEffect {
    data object Replay : PlayerEffect
}
