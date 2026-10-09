package com.shortdrama.ai.core.ui.extension

import android.content.Context
import android.graphics.Typeface
import android.text.SpannableStringBuilder
import android.text.TextPaint
import android.text.style.ForegroundColorSpan
import android.text.style.MetricAffectingSpan
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.core.text.inSpans
import com.shortdrama.ai.core.ui.R

/** "12/36 Ep" with the released count in the accent color and ExtraBold, as in the design. */
fun Context.formatEpisodes(released: Int, total: Int): CharSequence {
    val accent = ContextCompat.getColor(this, R.color.sf_episode_accent)
    val extraBold = ResourcesCompat.getFont(this, R.font.plus_jakarta_sans_extrabold)
    return SpannableStringBuilder()
        .inSpans(ForegroundColorSpan(accent), TypefaceSpanCompat(extraBold)) { append(released.toString()) }
        .append(getString(R.string.sf_episodes_total, total))
}

/** android.text.style.TypefaceSpan(Typeface) needs API 28. */
private class TypefaceSpanCompat(private val typeface: Typeface?) : MetricAffectingSpan() {
    override fun updateDrawState(paint: TextPaint) = apply(paint)
    override fun updateMeasureState(paint: TextPaint) = apply(paint)
    private fun apply(paint: TextPaint) {
        if (typeface != null) paint.typeface = typeface
    }
}
