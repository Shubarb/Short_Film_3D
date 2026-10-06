package com.shortdrama.ai.feature.player

import android.content.pm.ActivityInfo
import android.content.res.Configuration
import androidx.annotation.OptIn
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.fragment.app.viewModels
import androidx.media3.common.MediaItem
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.navigation.fragment.findNavController
import com.shortdrama.ai.core.model.Film
import com.shortdrama.ai.core.ui.base.BaseMviFragment
import com.shortdrama.ai.core.ui.extension.formatDuration
import com.shortdrama.ai.core.ui.extension.setVisibleOrGone
import com.shortdrama.ai.feature.player.databinding.FragmentPlayerBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PlayerFragment : BaseMviFragment<FragmentPlayerBinding, PlayerIntent, PlayerState, PlayerEffect>(
    FragmentPlayerBinding::inflate,
) {

    override val viewModel: PlayerViewModel by viewModels()

    private var player: ExoPlayer? = null

    private val isLandscape: Boolean
        get() = resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE

    private val playerListener = object : Player.Listener {
        override fun onPlayerError(error: PlaybackException) {
            dispatch(PlayerIntent.PlaybackFailed)
        }
    }

    @OptIn(UnstableApi::class)
    override fun setupViews() {
        super.setupViews()
        applySystemBars()
        binding.toolbar?.setNavigationOnClickListener { findNavController().navigateUp() }
        binding.buttonRetry.setOnClickListener { dispatch(PlayerIntent.Retry) }
        binding.playerView.setFullscreenButtonState(isLandscape)
        binding.playerView.setFullscreenButtonClickListener { enterFullscreen ->
            requireActivity().requestedOrientation = if (enterFullscreen) {
                ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE
            } else {
                ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
            }
        }
    }

    override fun render(state: PlayerState) {
        with(binding) {
            progressLoading.setVisibleOrGone(state.isLoading)
            groupError.setVisibleOrGone(state.isNotFound || state.hasPlaybackError)
            textError.setText(
                if (state.isNotFound) R.string.player_not_found else R.string.player_playback_error,
            )
            buttonRetry.setVisibleOrGone(state.hasPlaybackError)
            state.film?.let(::renderFilmInfo)
        }
        val film = state.film
        if (film != null && player == null) initializePlayer(film, state)
    }

    override fun handleEffect(effect: PlayerEffect) {
        when (effect) {
            PlayerEffect.Replay -> player?.run {
                prepare()
                play()
            }
        }
    }

    override fun onStop() {
        releasePlayer()
        super.onStop()
    }

    override fun onDestroyView() {
        // Leaving the screen (not rotating): give orientation and system bars back to the app.
        if (!requireActivity().isChangingConfigurations) {
            requireActivity().requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
            showSystemBars(true)
        }
        super.onDestroyView()
    }

    private fun renderFilmInfo(film: Film) = with(binding) {
        toolbar?.title = film.title
        textTitle?.text = film.title
        textMeta?.text = listOfNotNull(
            film.durationSeconds.takeIf { it > 0 }?.formatDuration(),
            film.tags.takeIf { it.isNotEmpty() }?.joinToString(" · ") { "#$it" },
        ).joinToString("   ")
        textDescription?.text = film.description
        textPremium?.setVisibleOrGone(film.isPremium)
    }

    private fun initializePlayer(film: Film, state: PlayerState) {
        player = ExoPlayer.Builder(requireContext()).build().also { exoPlayer ->
            binding.playerView.player = exoPlayer
            exoPlayer.addListener(playerListener)
            // MediaItem.fromUri picks the right source for .mp4 / .m3u8 (HLS module included).
            exoPlayer.setMediaItem(MediaItem.fromUri(film.videoUrl), state.playbackPositionMs)
            exoPlayer.playWhenReady = state.playWhenReady
            exoPlayer.prepare()
        }
    }

    private fun releasePlayer() {
        val exoPlayer = player ?: return
        dispatch(PlayerIntent.SavePlayback(exoPlayer.currentPosition, exoPlayer.playWhenReady))
        exoPlayer.removeListener(playerListener)
        exoPlayer.release()
        binding.playerView.player = null
        player = null
    }

    private fun applySystemBars() {
        showSystemBars(!isLandscape)
    }

    private fun showSystemBars(show: Boolean) {
        val window = requireActivity().window
        val controller = WindowCompat.getInsetsController(window, window.decorView)
        if (show) {
            controller.show(WindowInsetsCompat.Type.systemBars())
        } else {
            controller.systemBarsBehavior =
                WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
            controller.hide(WindowInsetsCompat.Type.systemBars())
        }
    }
}
