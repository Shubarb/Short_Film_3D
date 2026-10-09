package com.shortdrama.ai.core.ui.widget

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View
import com.shortdrama.ai.core.ui.R

/** Pill page indicator: the selected page is a wide pill, the others are dots. */
class PagerIndicatorView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
) : View(context, attrs, defStyleAttr) {

    private val dotSize: Float
    private val activeWidth: Float
    private val spacing: Float
    private val activePaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val inactivePaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val rect = RectF()

    var count: Int = 0
        set(value) {
            val coerced = value.coerceAtLeast(0)
            if (field == coerced) return
            field = coerced
            requestLayout()
            invalidate()
        }

    var selection: Int = 0
        set(value) {
            if (field == value) return
            field = value
            invalidate()
        }

    init {
        val density = resources.displayMetrics.density
        val a = context.obtainStyledAttributes(attrs, R.styleable.PagerIndicatorView, defStyleAttr, 0)
        dotSize = a.getDimension(R.styleable.PagerIndicatorView_indicatorDotSize, 6 * density)
        activeWidth = a.getDimension(R.styleable.PagerIndicatorView_indicatorActiveWidth, 18 * density)
        spacing = a.getDimension(R.styleable.PagerIndicatorView_indicatorSpacing, 4 * density)
        activePaint.color = a.getColor(R.styleable.PagerIndicatorView_indicatorActiveColor, Color.WHITE)
        inactivePaint.color = a.getColor(
            R.styleable.PagerIndicatorView_indicatorInactiveColor,
            Color.argb(89, 255, 255, 255),
        )
        a.recycle()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val contentWidth = if (count == 0) 0f else activeWidth + (count - 1) * (dotSize + spacing)
        setMeasuredDimension(
            resolveSize((contentWidth + paddingLeft + paddingRight).toInt(), widthMeasureSpec),
            resolveSize((dotSize + paddingTop + paddingBottom).toInt(), heightMeasureSpec),
        )
    }

    override fun onDraw(canvas: Canvas) {
        var left = paddingLeft.toFloat()
        val top = paddingTop.toFloat()
        val radius = dotSize / 2f
        for (index in 0 until count) {
            val active = index == selection
            val width = if (active) activeWidth else dotSize
            rect.set(left, top, left + width, top + dotSize)
            canvas.drawRoundRect(rect, radius, radius, if (active) activePaint else inactivePaint)
            left += width + spacing
        }
    }
}
