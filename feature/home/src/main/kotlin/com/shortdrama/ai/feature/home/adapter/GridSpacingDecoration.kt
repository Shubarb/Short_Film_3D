package com.shortdrama.ai.feature.home.adapter

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView

/** Gaps between the cells of a grid, none on its outer edges. */
class GridSpacingDecoration(
    private val spanCount: Int,
    private val horizontalSpacing: Int,
    private val verticalSpacing: Int,
) : RecyclerView.ItemDecoration() {

    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
        val position = parent.getChildAdapterPosition(view)
        if (position == RecyclerView.NO_POSITION) return
        val column = position % spanCount
        // Split each gap between its two neighbours so all cells keep the same width.
        outRect.left = column * horizontalSpacing / spanCount
        outRect.right = horizontalSpacing - (column + 1) * horizontalSpacing / spanCount
        outRect.top = if (position < spanCount) 0 else verticalSpacing
    }
}
