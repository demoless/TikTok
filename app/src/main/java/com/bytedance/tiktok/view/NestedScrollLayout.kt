package com.bytedance.tiktok.view

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.NestedScrollingParent2
import androidx.core.view.NestedScrollingParentHelper
import androidx.core.view.ViewCompat
import kotlinx.android.synthetic.main.activity_test.view.*

class NestedScrollLayout @JvmOverloads constructor(context: Context,attrs:AttributeSet?=null,defStyleAttr:Int=0)
    : ConstraintLayout(context,attrs,defStyleAttr),NestedScrollingParent2 {

    private val contentY by lazy {
        scroll_content.y
    }

    private var contentUpdateY:Float = 0f;

    private val topBarY by lazy { top_bar.y }

    private val topBarHeight by lazy { top_bar.height }

    private val topBarTranslationY by lazy { topBarY + topBarHeight}

    private val upChangeY by lazy {
        (contentY - top_bar.height)*1.5
    }

    private val mParentHelper: NestedScrollingParentHelper by lazy {
        NestedScrollingParentHelper(this)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        contentUpdateY = scroll_content.y
    }


    //NestedScrollingParent兼容 由NestedScrollingParent处理
    override fun onNestedScrollAccepted(child: View, target: View, axes: Int) {
        onNestedScrollAccepted(child, target, axes, ViewCompat.TYPE_TOUCH)
    }
    override fun onNestedScrollAccepted(child: View, target: View, axes: Int, type: Int) {
        mParentHelper.onNestedScrollAccepted(child, target, axes, type)
    }

    override fun onStartNestedScroll(child: View, target: View, nestedScrollAxes: Int): Boolean {
        return onStartNestedScroll(child, target, nestedScrollAxes,ViewCompat.TYPE_TOUCH)
    }

    override fun onStartNestedScroll(child: View, target: View, axes: Int, type: Int): Boolean {
        //只接受垂直方向上的滑动
        return child.id == scroll_content.id && axes == ViewCompat.SCROLL_AXIS_VERTICAL
    }


    override fun onNestedPreScroll(target: View, dx: Int, dy: Int, consumed: IntArray) {
        onNestedPreScroll(target, dx, dy, consumed,ViewCompat.TYPE_TOUCH)
    }

    override fun onNestedPreScroll(target: View, dx: Int, dy: Int, consumed: IntArray, type: Int) {
        val upY = contentY - upChangeY
        //处理上滑
        if (dy > 0) {
            if (dy <= upY) {
                consumeTransY(scroll_content,dy.toFloat(),-dy.toFloat(),consumed)
            } else {
                consumeTransY(scroll_content,upY.toFloat(),-upY.toFloat(),consumed)
            }
        }
        if (dy < 0 && !target.canScrollVertically(-1)) {

            consumeTransY(scroll_content,dy.toFloat(),-dy.toFloat(),consumed)
        }
    }

    override fun onNestedScroll(target: View, dxConsumed: Int, dyConsumed: Int, dxUnconsumed: Int, dyUnconsumed: Int, type: Int) {
        super.onNestedScroll(target, dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed)
    }

    override fun onStopNestedScroll(target: View, type: Int) {
        super.onStopNestedScroll(target)
    }

    private fun consumeTransY(target: View,consumed: Float,translationY: Float,dyConsumed: IntArray) {
        dyConsumed[1] = consumed.toInt()
        target.translationY = translationY
        Log.e("--message","target.translationY:"+ translationY+"\n")
    }

}