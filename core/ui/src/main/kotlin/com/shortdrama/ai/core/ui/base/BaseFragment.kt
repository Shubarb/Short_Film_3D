package com.shortdrama.ai.core.ui.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.viewbinding.ViewBinding

typealias Inflate<VB> = (LayoutInflater, ViewGroup?, Boolean) -> VB

/** Handles the ViewBinding lifecycle so subclasses never leak a view. */
abstract class BaseFragment<VB : ViewBinding>(
    private val inflate: Inflate<VB>,
) : Fragment() {

    private var _binding: VB? = null
    protected val binding: VB
        get() = checkNotNull(_binding) { "Binding accessed outside the view lifecycle" }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupViews()
    }

    /** Wire listeners, adapters, insets. Called once per view creation. */
    protected open fun setupViews() = Unit

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }
}
