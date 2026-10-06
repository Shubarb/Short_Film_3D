package com.shortdrama.ai.core.ui.extension

import android.widget.ImageView
import coil.load
import com.shortdrama.ai.core.ui.R

/** Single place that knows about the image loader (Coil), so features stay library-agnostic. */
fun ImageView.loadImage(url: String?) {
    load(url) {
        crossfade(true)
        placeholder(R.drawable.bg_thumbnail_placeholder)
        error(R.drawable.bg_thumbnail_placeholder)
    }
}
