package com.shortdrama.ai.core.ui.extension

import android.view.View

fun View.setVisibleOrGone(visible: Boolean) {
    visibility = if (visible) View.VISIBLE else View.GONE
}
