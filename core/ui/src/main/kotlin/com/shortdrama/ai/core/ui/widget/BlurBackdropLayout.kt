package com.shortdrama.ai.core.ui.widget

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Outline
import android.graphics.Paint
import android.graphics.RectF
import android.graphics.RenderEffect
import android.graphics.RenderNode
import android.graphics.Shader
import android.os.Build
import android.util.AttributeSet
import android.view.View
import android.view.ViewOutlineProvider
import android.view.ViewTreeObserver
import android.widget.FrameLayout
import androidx.annotation.RequiresApi
import com.shortdrama.ai.core.ui.R

/**
 * Rounded "frosted glass" container (CSS `backdrop-filter: blur()`): it shows the content of a
 * [BlurTargetLayout] that lies behind it, blurred, then [R.styleable.BlurBackdropLayout_blurOverlayColor]
 * and its children on top.
 *
 * The blur needs Android 12 (RenderEffect). Older versions draw
 * [R.styleable.BlurBackdropLayout_blurFallbackColor] instead.
 *
 * Must not be a descendant of its target; place it as a later sibling so it draws after it.
 */
class BlurBackdropLayout @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
) : FrameLayout(context, attrs, defStyleAttr) {

    private val blurRadius: Float
    private val cornerRadius: Float
    private val overlayPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val fallbackPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val strokePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply { style = Paint.Style.STROKE }
    private val fillBounds = RectF()
    private val strokeBounds = RectF()

    private var blurTarget: BlurTargetLayout? = null
    private var renderer: BackdropRenderer? = null

    /** The target re-records itself on every frame it changes; follow it. */
    private val preDrawListener = ViewTreeObserver.OnPreDrawListener {
        if (blurTarget != null && isShown) invalidate()
        true
    }

    init {
        val a = context.obtainStyledAttributes(attrs, R.styleable.BlurBackdropLayout, defStyleAttr, 0)
        blurRadius = a.getDimension(R.styleable.BlurBackdropLayout_blurRadius, 0f)
        cornerRadius = a.getDimension(R.styleable.BlurBackdropLayout_blurCornerRadius, 0f)
        overlayPaint.color = a.getColor(R.styleable.BlurBackdropLayout_blurOverlayColor, Color.TRANSPARENT)
        fallbackPaint.color = a.getColor(R.styleable.BlurBackdropLayout_blurFallbackColor, overlayPaint.color)
        strokePaint.color = a.getColor(R.styleable.BlurBackdropLayout_blurStrokeColor, Color.TRANSPARENT)
        strokePaint.strokeWidth = a.getDimension(R.styleable.BlurBackdropLayout_blurStrokeWidth, 0f)
        a.recycle()
        setWillNotDraw(false)
        outlineProvider = object : ViewOutlineProvider() {
            override fun getOutline(view: View, outline: Outline) {
                outline.setRoundRect(0, 0, view.width, view.height, cornerRadius)
            }
        }
        clipToOutline = true
    }

    /** Blur the content of [target]. Pass null to stop blurring. */
    fun setBlurTarget(target: BlurTargetLayout?) {
        blurTarget = target
        if (target != null && renderer == null &&
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && blurRadius > 0f
        ) {
            renderer = BackdropRenderer(blurRadius)
        }
        invalidate()
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        viewTreeObserver.addOnPreDrawListener(preDrawListener)
    }

    override fun onDetachedFromWindow() {
        viewTreeObserver.removeOnPreDrawListener(preDrawListener)
        super.onDetachedFromWindow()
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        fillBounds.set(0f, 0f, w.toFloat(), h.toFloat())
        val half = strokePaint.strokeWidth / 2f
        strokeBounds.set(half, half, w - half, h - half)
    }

    override fun draw(canvas: Canvas) {
        val target = blurTarget
        val renderer = renderer
        val blurred = target != null && renderer != null && canvas.isHardwareAccelerated &&
            renderer.draw(canvas, this, target)
        canvas.drawRoundRect(fillBounds, cornerRadius, cornerRadius, if (blurred) overlayPaint else fallbackPaint)
        super.draw(canvas)
        if (strokePaint.strokeWidth > 0f) {
            val radius = (cornerRadius - strokePaint.strokeWidth / 2f).coerceAtLeast(0f)
            canvas.drawRoundRect(strokeBounds, radius, radius, strokePaint)
        }
    }
}

@RequiresApi(Build.VERSION_CODES.S)
private class BackdropRenderer(radius: Float) {
    private val node = RenderNode("BlurBackdrop").apply {
        setRenderEffect(RenderEffect.createBlurEffect(radius, radius, Shader.TileMode.CLAMP))
    }
    private val hostLocation = IntArray(2)
    private val targetLocation = IntArray(2)

    /** Returns false when the target has nothing recorded yet. */
    fun draw(canvas: Canvas, host: View, target: BlurTargetLayout): Boolean {
        val content = target.recorder?.node ?: return false
        if (!content.hasDisplayList()) return false
        host.getLocationInWindow(hostLocation)
        target.getLocationInWindow(targetLocation)
        node.setPosition(0, 0, host.width, host.height)
        val recording = node.beginRecording()
        recording.translate(
            (targetLocation[0] - hostLocation[0]).toFloat(),
            (targetLocation[1] - hostLocation[1]).toFloat(),
        )
        recording.drawRenderNode(content)
        node.endRecording()
        canvas.drawRenderNode(node)
        return true
    }
}
