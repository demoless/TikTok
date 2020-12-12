package com.bytedance.tiktok.view

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.os.Build
import android.util.AttributeSet
import android.view.View
import com.bytedance.tiktok.R
import kotlin.math.min

class StrokeCircleView @JvmOverloads constructor(
        context: Context,
        attrs: AttributeSet? = null,
        defStyleAttr: Int = 0) : View(context,attrs,defStyleAttr) {
    private var innerR: Float = 100f
        set(value) {
            field = value * innerR
            invalidate()
        }

    private var strokeWidth = 10f
        set(value) {
            field = value
            circlePaint.strokeWidth = value * 10f
            invalidate()
        }

    private val circlePaint by lazy {
        Paint().apply {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                color = context.getColor(R.color.black)
            }
            style = Paint.Style.STROKE
            //alpha = outSideAlpha
            strokeWidth = this@StrokeCircleView.strokeWidth
        }
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        val r = min(width,height)
    }
}