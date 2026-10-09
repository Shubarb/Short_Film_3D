package com.shortdrama.ai.feature.home.adapter

import androidx.recyclerview.widget.DiffUtil
import com.shortdrama.ai.core.model.Film

internal object FilmDiff : DiffUtil.ItemCallback<Film>() {
    override fun areItemsTheSame(oldItem: Film, newItem: Film) = oldItem.id == newItem.id
    override fun areContentsTheSame(oldItem: Film, newItem: Film) = oldItem == newItem
}
