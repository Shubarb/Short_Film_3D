package com.shortdrama.ai.feature.home

import android.view.View
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.google.android.material.chip.Chip
import com.google.android.material.snackbar.Snackbar
import com.shortdrama.ai.core.model.Category
import com.shortdrama.ai.core.ui.base.BaseMviFragment
import com.shortdrama.ai.core.ui.extension.setVisibleOrGone
import com.shortdrama.ai.core.ui.navigation.navigateToPlayer
import com.shortdrama.ai.feature.home.adapter.FilmAdapter
import com.shortdrama.ai.feature.home.databinding.FragmentHomeBinding
import dagger.hilt.android.AndroidEntryPoint
import com.shortdrama.ai.core.ui.R as CoreUiR

@AndroidEntryPoint
class HomeFragment : BaseMviFragment<FragmentHomeBinding, HomeIntent, HomeState, HomeEffect>(
    FragmentHomeBinding::inflate,
) {

    override val viewModel: HomeViewModel by viewModels()

    private val filmAdapter = FilmAdapter { film -> dispatch(HomeIntent.OpenFilm(film.id)) }

    /** Chips are rebuilt only when categories change, not on every state emission. */
    private var renderedCategories: List<Category>? = null

    override fun setupViews() {
        super.setupViews()
        renderedCategories = null
        with(binding) {
            recyclerFilms.layoutManager = GridLayoutManager(requireContext(), SPAN_COUNT)
            recyclerFilms.adapter = filmAdapter
            swipeRefresh.setOnRefreshListener { dispatch(HomeIntent.Refresh) }
            buttonRetry.setOnClickListener { dispatch(HomeIntent.Refresh) }
            chipGroupCategories.setOnCheckedStateChangeListener { group, checkedIds ->
                val chip = checkedIds.firstOrNull()?.let { group.findViewById<Chip>(it) }
                val categoryId = chip?.tag as? String
                if (categoryId != viewModel.state.value.selectedCategoryId) {
                    dispatch(HomeIntent.SelectCategory(categoryId))
                }
            }
        }
    }

    override fun render(state: HomeState) = with(binding) {
        progressLoading.setVisibleOrGone(state.isLoading)
        swipeRefresh.isRefreshing = state.isRefreshing
        groupEmpty.setVisibleOrGone(state.isEmpty)
        renderCategories(state.categories, state.selectedCategoryId)
        filmAdapter.submitList(state.films)
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
        binding.recyclerFilms.adapter = null
        super.onDestroyView()
    }

    private fun renderCategories(categories: List<Category>, selectedId: String?) {
        val group = binding.chipGroupCategories
        if (categories != renderedCategories) {
            renderedCategories = categories
            group.removeAllViews()
            group.addView(createChip(tag = null, title = getString(R.string.home_category_all)))
            categories.forEach { group.addView(createChip(tag = it.id, title = it.title)) }
        }
        binding.scrollCategories.setVisibleOrGone(categories.isNotEmpty())
        val target = (0 until group.childCount)
            .map { group.getChildAt(it) as Chip }
            .firstOrNull { it.tag == selectedId }
        if (target != null && !target.isChecked) target.isChecked = true
    }

    private fun createChip(tag: String?, title: String): Chip =
        (layoutInflater.inflate(R.layout.item_category_chip, binding.chipGroupCategories, false) as Chip)
            .apply {
                id = View.generateViewId()
                this.tag = tag
                text = title
            }

    private companion object {
        const val SPAN_COUNT = 2
    }
}
