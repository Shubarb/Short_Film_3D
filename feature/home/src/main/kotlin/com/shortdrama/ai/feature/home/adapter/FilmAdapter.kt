package com.shortdrama.ai.feature.home.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.shortdrama.ai.core.model.Film
import com.shortdrama.ai.core.ui.extension.formatDuration
import com.shortdrama.ai.core.ui.extension.setVisibleOrGone
import com.shortdrama.ai.core.ui.extension.loadImage
import com.shortdrama.ai.feature.home.databinding.ItemFilmBinding

class FilmAdapter(
    private val onFilmClick: (Film) -> Unit,
) : ListAdapter<Film, FilmAdapter.FilmViewHolder>(FilmDiff) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FilmViewHolder {
        val binding = ItemFilmBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return FilmViewHolder(binding)
    }

    override fun onBindViewHolder(holder: FilmViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class FilmViewHolder(
        private val binding: ItemFilmBinding,
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(film: Film) = with(binding) {
            imageThumbnail.loadImage(film.thumbnailUrl)
            textTitle.text = film.title
            textDuration.text = film.durationSeconds.formatDuration()
            textDuration.setVisibleOrGone(film.durationSeconds > 0)
            textPremium.setVisibleOrGone(film.isPremium)
            root.setOnClickListener { onFilmClick(film) }
        }
    }

    private object FilmDiff : DiffUtil.ItemCallback<Film>() {
        override fun areItemsTheSame(oldItem: Film, newItem: Film) = oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: Film, newItem: Film) = oldItem == newItem
    }
}
