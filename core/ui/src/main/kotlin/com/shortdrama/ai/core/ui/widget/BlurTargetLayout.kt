package com.shortdrama.ai.core.ui.widget

import android.content.Context
import android.graphics.Canvas
import android.graphics.RenderNode
import android.os.Build
import android.util.AttributeSet
import android.widget.FrameLayout
import androidx.annotation.RequiresApi

/**
 * Container whose content can be shown, blurred, behind a [BlurBackdropLayout] drawn on top of it.
 *
 * On Android 12+ it records its content into a RenderNode that the backdrop re-draws with a
 * blur effect, so the blur follows scrolling and animations without copying bitmaps.
 * Below Android 12 it behaves like a plain FrameLayout.
 */
class BlurTargetLayout @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
) : FrameLayout(context, attrs, defStyleAttr) {

    internal val recorder: ContentRecorder? =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) ContentRecorder() else null

    init {
        // A ViewGroup that "will not draw" skips draw() and calls dispatchDraw() directly,
        // which would bypass the recording below.
        setWillNotDraw(false)
    }

    override fun draw(canvas: Canvas) {
        val recorder = recorder
        if (recorder == null || !canvas.isHardwareAccelerated) {
            super.draw(canvas)
            return
        }
        super.draw(recorder.beginRecording(width, height))
        recorder.endRecording(canvas)
    }
}

/** Keeps every RenderNode reference out of classes that load on Android < 10. */
@RequiresApi(Build.VERSION_CODES.S)
internal class ContentRecorder {
    val node = RenderNode("BlurTargetContent")

    fun beginRecording(width: Int, height: Int): Canvas {
        node.setPosition(0, 0, width, height)
        return node.beginRecording()
    }

    fun endRecording(canvas: Canvas) {
        node.endRecording()
        canvas.drawRenderNode(node)
    }
}
