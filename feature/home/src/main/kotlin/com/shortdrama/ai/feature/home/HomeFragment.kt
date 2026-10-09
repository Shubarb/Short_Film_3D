package com.shortdrama.ai.feature.home

import android.view.View
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.snackbar.Snackbar
import com.shortdrama.ai.core.model.Film
import com.shortdrama.ai.core.ui.base.BaseMviFragment
import com.shortdrama.ai.core.ui.extension.setVisibleOrGone
import com.shortdrama.ai.core.ui.navigation.navigateToPlayer
import com.shortdrama.ai.feature.home.adapter.BannerAdapter
import com.shortdrama.ai.feature.home.adapter.FilmCardAdapter
import com.shortdrama.ai.feature.home.adapter.GridSpacingDecoration
import com.shortdrama.ai.feature.home.databinding.FragmentHomeBinding
import dagger.hilt.android.AndroidEntryPoint
import java.util.Calendar
import java.util.Locale
import com.shortdrama.ai.core.ui.R as CoreUiR

/** First tab of the Main screen: banner carousel, "Big Hit" and "Recommend For You". */
@AndroidEntryPoint
class HomeFragment : BaseMviFragment<FragmentHomeBinding, HomeIntent, HomeState, HomeEffect>(
    FragmentHomeBinding::inflate,
) {

    override val viewModel: HomeViewModel by viewModels()

    private val openFilm: (Film) -> Unit = { film -> dispatch(HomeIntent.OpenFilm(film.id)) }
    private val bannerAdapter = BannerAdapter(openFilm)
    private val bigHitAdapter = FilmCardAdapter(FilmCardAdapter.Style.LANDSCAPE, openFilm)
    private val recommendAdapter = FilmCardAdapter(FilmCardAdapter.Style.POSTER, openFilm)

    private val bannerPageCallback = object : ViewPager2.OnPageChangeCallback() {
        override fun onPageSelected(position: Int) {
            if (position != viewModel.state.value.bannerPosition) {
                dispatch(HomeIntent.BannerChanged(position))
            }
        }
    }

    override fun setupViews() {
        super.setupViews()
        with(binding) {
            ViewCompat.setOnApplyWindowInsetsListener(layoutTopBar) { view, insets ->
                val statusBars = insets.getInsets(WindowInsetsCompat.Type.statusBars())
                view.updatePadding(top = resources.getDimensionPixelSize(R.dimen.home_top_bar_padding) + statusBars.top)
                insets
            }
            ViewCompat.requestApplyInsets(layoutTopBar)
            textGreeting.setText(greetingRes())

            pagerBanner.adapter = bannerAdapter
            pagerBanner.registerOnPageChangeCallback(bannerPageCallback)

            val spacing = resources.getDimensionPixelSize(CoreUiR.dimen.sf_screen_padding)
            listOf(recyclerBigHit to bigHitAdapter, recyclerRecommend to recommendAdapter)
                .forEach { (recycler, adapter) ->
                    recycler.layoutManager = GridLayoutManager(requireContext(), SPAN_COUNT)
                    recycler.addItemDecoration(GridSpacingDecoration(SPAN_COUNT, spacing, spacing))
                    recycler.adapter = adapter
                }

            homeTabs().forEach { (tab, view) ->
                view.setOnClickListener { dispatch(HomeIntent.SelectTab(tab)) }
            }
            swipeRefresh.setProgressViewOffset(false, 0, resources.getDimensionPixelSize(R.dimen.home_refresh_offset))
            swipeRefresh.setOnRefreshListener { dispatch(HomeIntent.Refresh) }
            buttonRetry.setOnClickListener { dispatch(HomeIntent.Refresh) }
        }
    }

    override fun render(state: HomeState) = with(binding) {
        swipeRefresh.isRefreshing = state.isRefreshing
        homeTabs().forEach { (tab, view) -> view.isSelected = tab == state.selectedTab }

        bannerAdapter.submitList(state.feed.banners) {
            // Restore the page after the pages exist (e.g. coming back from the player).
            if (pagerBanner.currentItem != state.bannerPosition) {
                pagerBanner.setCurrentItem(state.bannerPosition, false)
            }
        }
        indicatorBanner.count = state.feed.banners.size
        indicatorBanner.selection = state.bannerPosition
        renderBannerInfo(state.currentBanner)

        bigHitAdapter.submitList(state.feed.bigHits)
        recommendAdapter.submitList(state.feed.recommended)
        textSectionBigHit.setVisibleOrGone(state.feed.bigHits.isNotEmpty())
        recyclerBigHit.setVisibleOrGone(state.feed.bigHits.isNotEmpty())
        textSectionRecommend.setVisibleOrGone(state.feed.recommended.isNotEmpty())
        recyclerRecommend.setVisibleOrGone(state.feed.recommended.isNotEmpty())
        textEmpty.setVisibleOrGone(state.isEmpty)
        buttonRetry.setVisibleOrGone(state.isEmpty)
    }

    override fun handleEffect(effect: HomeEffect) {
        when (effect) {
            is HomeEffect.NavigateToPlayer -> findNavController().navigateToPlayer(effect.filmId)
            HomeEffect.ShowRefreshError -> Snackbar.make(
                binding.root,
                CoreUiR.string.sf_error_network,
                Snackbar.LENGTH_LONG,
            ).show()
        }
    }

    override fun onDestroyView() {
        with(binding) {
            pagerBanner.unregisterOnPageChangeCallback(bannerPageCallback)
            pagerBanner.adapter = null
            recyclerBigHit.adapter = null
            recyclerRecommend.adapter = null
        }
        super.onDestroyView()
    }

    private fun renderBannerInfo(film: Film?) = with(binding) {
        layoutBannerInfo.setVisibleOrGone(film != null)
        if (film == null) return@with
        textBannerRating.setVisibleOrGone(film.rating != null)
        textBannerRating.text = film.rating?.let { String.format(Locale.US, "%.1f", it) }
        textBannerTitle.text = film.title
        textBannerGenres.text = film.tags.joinToString(getString(R.string.home_genre_separator))
        textBannerGenres.setVisibleOrGone(film.tags.isNotEmpty())
    }

    private fun homeTabs(): List<Pair<HomeTab, View>> = with(binding) {
        listOf(
            HomeTab.FOR_YOU to tabForYou,
            HomeTab.NEW to tabNew,
            HomeTab.ORIGINAL to tabOriginal,
            HomeTab.RANKING to tabRanking,
            HomeTab.CATEGORY to tabCategory,
        )
    }

    private fun greetingRes(): Int = when (Calendar.getInstance().get(Calendar.HOUR_OF_DAY)) {
        in 5..11 -> R.string.home_greeting_morning
        in 12..17 -> R.string.home_greeting_afternoon
        else -> R.string.home_greeting_evening
    }

    private companion object {
        const val SPAN_COUNT = 2
    }
}
