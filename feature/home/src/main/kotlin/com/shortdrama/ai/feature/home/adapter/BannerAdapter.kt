package com.shortdrama.ai.feature.home.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.shortdrama.ai.core.model.Film
import com.shortdrama.ai.core.ui.extension.loadImage
import com.shortdrama.ai.feature.home.databinding.ItemHomeBannerBinding

/** Full-bleed portrait artwork pages of the Home banner. */
class BannerAdapter(
    private val onFilmClick: (Film) -> Unit,
) : ListAdapter<Film, BannerAdapter.BannerViewHolder>(FilmDiff) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BannerViewHolder {
        val binding = ItemHomeBannerBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return BannerViewHolder(binding)
    }

    override fun onBindViewHolder(holder: BannerViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class BannerViewHolder(
        private val binding: ItemHomeBannerBinding,
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(film: Film) = with(binding) {
            imageBanner.loadImage(film.posterUrl)
            imageBanner.contentDescription = film.title
            root.setOnClickListener { onFilmClick(film) }
        }
    }
}
