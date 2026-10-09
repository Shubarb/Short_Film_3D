package com.shortdrama.ai.feature.home.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import com.shortdrama.ai.core.model.Film
import com.shortdrama.ai.core.ui.extension.formatEpisodes
import com.shortdrama.ai.core.ui.extension.loadImage
import com.shortdrama.ai.core.ui.extension.setVisibleOrGone
import com.shortdrama.ai.feature.home.R
import com.shortdrama.ai.feature.home.databinding.ItemFilmLandscapeBinding
import com.shortdrama.ai.feature.home.databinding.ItemFilmPosterBinding

/** Film cards of the Home sections: landscape ("Big Hit") or poster ("Recommend For You"). */
class FilmCardAdapter(
    private val style: Style,
    private val onFilmClick: (Film) -> Unit,
) : ListAdapter<Film, FilmCardAdapter.CardViewHolder>(FilmDiff) {

    enum class Style { LANDSCAPE, POSTER }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CardViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return when (style) {
            Style.LANDSCAPE -> LandscapeViewHolder(ItemFilmLandscapeBinding.inflate(inflater, parent, false))
            Style.POSTER -> PosterViewHolder(ItemFilmPosterBinding.inflate(inflater, parent, false))
        }
    }

    override fun onBindViewHolder(holder: CardViewHolder, position: Int) {
        val film = getItem(position)
        holder.bind(film)
        holder.itemView.setOnClickListener { onFilmClick(film) }
    }

    abstract class CardViewHolder(binding: ViewBinding) : RecyclerView.ViewHolder(binding.root) {
        abstract fun bind(film: Film)

        protected fun bindCommon(film: Film, title: TextView, genres: TextView, episodes: TextView) {
            val context = itemView.context
            title.text = film.title
            genres.text = film.tags.joinToString(context.getString(R.string.home_tag_separator))
            genres.setVisibleOrGone(film.tags.isNotEmpty())
            episodes.setVisibleOrGone(film.totalEpisodes > 0)
            if (film.totalEpisodes > 0) {
                episodes.text = context.formatEpisodes(film.releasedEpisodes, film.totalEpisodes)
            }
        }
    }

    private class LandscapeViewHolder(
        private val binding: ItemFilmLandscapeBinding,
    ) : CardViewHolder(binding) {
        override fun bind(film: Film) = with(binding) {
            imageThumbnail.loadImage(film.thumbnailUrl)
            bindCommon(film, textTitle, textGenres, textEpisodes)
        }
    }

    private class PosterViewHolder(
        private val binding: ItemFilmPosterBinding,
    ) : CardViewHolder(binding) {
        override fun bind(film: Film) = with(binding) {
            imagePoster.loadImage(film.posterUrl)
            textBadge.text = film.badge
            textBadge.setVisibleOrGone(film.badge != null)
            bindCommon(film, textTitle, textGenres, textEpisodes)
        }
    }
}
