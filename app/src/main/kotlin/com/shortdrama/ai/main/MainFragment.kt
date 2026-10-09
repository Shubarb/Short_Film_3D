package com.shortdrama.ai.main

import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updateLayoutParams
import androidx.fragment.app.Fragment
import androidx.fragment.app.commitNow
import androidx.fragment.app.viewModels
import com.shortdrama.ai.R
import com.shortdrama.ai.core.ui.base.BaseMviFragment
import com.shortdrama.ai.databinding.FragmentMainBinding
import com.shortdrama.ai.feature.home.HomeFragment
import com.shortdrama.ai.feature.library.LibraryFragment
import com.shortdrama.ai.feature.settings.SettingsFragment
import com.shortdrama.ai.feature.shorts.ShortsFragment
import dagger.hilt.android.AndroidEntryPoint
import com.shortdrama.ai.core.ui.R as CoreUiR

/**
 * Hosts one child fragment per bottom navigation tab. Tabs are added once and then shown/hidden,
 * so each keeps its scroll position and state while another tab is selected.
 */
@AndroidEntryPoint
class MainFragment : BaseMviFragment<FragmentMainBinding, MainIntent, MainState, MainEffect>(
    FragmentMainBinding::inflate,
) {

    override val viewModel: MainViewModel by viewModels()

    /** Back on another tab returns to Home before leaving the app. */
    private val backToHome = object : OnBackPressedCallback(false) {
        override fun handleOnBackPressed() = dispatch(MainIntent.SelectTab(MainTab.HOME))
    }

    override fun setupViews() {
        super.setupViews()
        with(binding) {
            bottomNav.setBlurTarget(blurTarget)
            tabViews().forEach { (tab, view) ->
                view.setOnClickListener { dispatch(MainIntent.SelectTab(tab)) }
            }
            val baseMargin = resources.getDimensionPixelSize(CoreUiR.dimen.sf_bottom_nav_margin_bottom)
            ViewCompat.setOnApplyWindowInsetsListener(bottomNav) { view, insets ->
                val navigationBars = insets.getInsets(WindowInsetsCompat.Type.navigationBars())
                view.updateLayoutParams<ViewGroup.MarginLayoutParams> {
                    bottomMargin = baseMargin + navigationBars.bottom
                }
                insets
            }
            ViewCompat.requestApplyInsets(bottomNav)
        }
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, backToHome)
    }

    override fun render(state: MainState) {
        showTab(state.selectedTab)
        tabViews().forEach { (tab, view) -> view.isSelected = tab == state.selectedTab }
        backToHome.isEnabled = state.selectedTab != MainTab.HOME
    }

    private fun showTab(selected: MainTab) {
        val fragments = childFragmentManager
        val current = fragments.findFragmentByTag(selected.name)
        if (current != null && !current.isHidden &&
            MainTab.entries.all { it == selected || fragments.findFragmentByTag(it.name)?.isHidden != false }
        ) {
            return
        }
        // commitNow: the next render must see this transaction, or a fast tab switch could add a tab twice.
        fragments.commitNow {
            setReorderingAllowed(true)
            MainTab.entries.forEach { tab ->
                val fragment = fragments.findFragmentByTag(tab.name)
                when {
                    tab == selected && fragment == null ->
                        add(R.id.container_tabs, createTabFragment(tab), tab.name)
                    tab == selected -> show(fragment!!)
                    fragment != null && !fragment.isHidden -> hide(fragment)
                }
            }
        }
    }

    private fun createTabFragment(tab: MainTab): Fragment = when (tab) {
        MainTab.HOME -> HomeFragment()
        MainTab.SHORT -> ShortsFragment()
        MainTab.LIBRARY -> LibraryFragment()
        MainTab.SETTING -> SettingsFragment()
    }

    private fun tabViews(): List<Pair<MainTab, View>> = with(binding) {
        listOf(
            MainTab.HOME to tabHome,
            MainTab.SHORT to tabShort,
            MainTab.LIBRARY to tabLibrary,
            MainTab.SETTING to tabSetting,
        )
    }
}
