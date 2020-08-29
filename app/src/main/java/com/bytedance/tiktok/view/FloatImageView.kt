package com.bytedance.tiktok.view

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.os.Build
import android.util.AttributeSet
import android.widget.ImageView
import com.bytedance.tiktok.R
import java.lang.Math.max
import java.lang.Math.min

@SuppressLint("AppCompatCustomView")
class FloatImageView @JvmOverloads constructor(
        context: Context,
        attrs: AttributeSet? = null,
        defStyleAttr: Int = 0) : ImageView(context,attrs,defStyleAttr) {

    private var isAnimating: Boolean = false
    private var r = 60f
    private var centerX: Int = 0
    private var centerY: Int = 0
    private var innerR: Float = 1f
        set(value) {
            field = value
            postInvalidate()
        }

    private var outSideAlpha = 1f
        set(value) {
            field = value
            postInvalidate()
        }

    private var strokeWidth = 20f
        set(value) {
            field = value * 20
            invalidate()
        }

    private val circlePaint by lazy {
         Paint().apply {
             if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                 color = context.getColor(R.color.black)
             }
             style = Paint.Style.STROKE
         }
    }

    fun startAnimate() {
        if (!isAnimating) {
            isAnimating = true
            postInvalidate()
        }
    }


    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        var width = MeasureSpec.getSize(widthMeasureSpec)
        var height = MeasureSpec.getSize(heightMeasureSpec)
        width = max(width,r.toInt())
        height = max(height,r.toInt())
        val widthMode = MeasureSpec.getMode(widthMeasureSpec)
        val heightMode = MeasureSpec.getMode(heightMeasureSpec)
        val widthMeasureSpec = MeasureSpec.makeMeasureSpec(width,widthMode)
        val heightMeasureSpec = MeasureSpec.makeMeasureSpec(height,heightMode)
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        val width = right - left
        val height = bottom - top
        val center = min(width,height).div(2).toFloat()
        r = center * innerR + strokeWidth
        circlePaint.alpha = (this.outSideAlpha * 255).toInt()
        circlePaint.strokeWidth = this.strokeWidth
        canvas?.drawCircle(center,center,r,circlePaint)
    }
}