package com.shortdrama.ai.feature.player

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.shortdrama.ai.core.domain.usecase.GetFilmUseCase
import com.shortdrama.ai.core.ui.mvi.MviViewModel
import com.shortdrama.ai.core.ui.navigation.DeepLinks
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PlayerViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val getFilm: GetFilmUseCase,
) : MviViewModel<PlayerIntent, PlayerState, PlayerEffect>(PlayerState()) {

    private val filmId: String = savedStateHandle.get<String>(DeepLinks.ARG_FILM_ID).orEmpty()

    init {
        viewModelScope.launch {
            val film = getFilm(filmId)
            setState { copy(isLoading = false, film = film, isNotFound = film == null) }
        }
    }

    override fun handleIntent(intent: PlayerIntent) {
        when (intent) {
            is PlayerIntent.SavePlayback -> setState {
                copy(playbackPositionMs = intent.positionMs, playWhenReady = intent.playWhenReady)
            }
            PlayerIntent.PlaybackFailed -> setState { copy(hasPlaybackError = true) }
            PlayerIntent.Retry -> {
                setState { copy(hasPlaybackError = false) }
                sendEffect(PlayerEffect.Replay)
            }
        }
    }
}
