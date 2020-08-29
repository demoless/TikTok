package com.bytedance.tiktok.view.floatView


import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.os.Build
import android.util.AttributeSet
import android.view.View
import android.widget.FrameLayout
import com.bytedance.tiktok.R
import kotlin.math.max


@SuppressLint("ObjectAnimatorBinding", "AnimatorKeep")
class FloatBarLayout @JvmOverloads constructor(
        context: Context,
        attrs: AttributeSet? = null,
        defStyleAttr: Int = 0) : FrameLayout(context,attrs,defStyleAttr) {
    companion object {
        private const val AnimatorDuration = 830L
        private const val STROKE_WIDTH = 4f
        private const val ALPHA = 255
    }

    init {
        setWillNotDraw(false)
    }

    private var imageViewWidth = 0

    private var childScale = 0f
        set(value) {
            field = value
            getChildren().scaleX = field
            getChildren().scaleY = field
        }


    private var stroke = 2f
        set (value) {
            field = value
            invalidate()
        }
    private var circleScaleSize = 1f
        set(value) {
            field = value
            invalidate()
        }
    private var circleAlpha = 0f
        set(value) {
            field = value
            invalidate()
        }

    private val outSidePaint: Paint by lazy {
        Paint().apply {
            style = Paint.Style.STROKE
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                color = context.getColor(R.color.white)
            }
        }
    }

    private  val imageViewAnimate: ObjectAnimator by lazy {
        ObjectAnimator.ofFloat(this,"childScale",0f,1f).apply {
            interpolator = ImageViewInterpolator()
            repeatMode = ValueAnimator.RESTART
            repeatCount = ValueAnimator.INFINITE
            duration = AnimatorDuration
        }
    }

    private val circleAlphaAnimate by lazy {
        ObjectAnimator.ofFloat(this,"circleAlpha",0f,1f).apply {
            interpolator = CircleAlphaInterpolator()
            repeatCount = ObjectAnimator.INFINITE
            repeatMode = ObjectAnimator.RESTART
            duration = AnimatorDuration
        }
    }

    private val circleScaleSizeAnimate by lazy {
        ObjectAnimator.ofFloat(this,"circleScaleSize",0f,1f).apply {
            interpolator = CircleScaleInterpolator()
            repeatCount = ObjectAnimator.INFINITE
            repeatMode = ObjectAnimator.RESTART
            duration = AnimatorDuration
        }
    }

    private val strokeWidthAnimate by lazy {
        ObjectAnimator.ofFloat(this,"stroke",0f,1f).apply {
            interpolator = CircleStrokeWidthInterpolator()
            repeatCount = ObjectAnimator.INFINITE
            repeatMode = ObjectAnimator.RESTART
            duration = AnimatorDuration
        }
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        outSidePaint.strokeWidth = STROKE_WIDTH * stroke
        outSidePaint.alpha = (ALPHA * circleAlpha).toInt()
        canvas.drawCircle(measuredWidth.div(2).toFloat(), measuredHeight.div(2).toFloat(),
                circleScaleSize * getChildren().width.div(2), outSidePaint)
    }

    private fun getChildren(): View {
        return getChildAt(0)
    }

    private val animateSet by lazy {
        AnimatorSet().apply {
            play(imageViewAnimate).with(circleAlphaAnimate).with(circleScaleSizeAnimate).with(strokeWidthAnimate)
        }
    }

    fun startAnim() {
        animateSet.start()
    }
}