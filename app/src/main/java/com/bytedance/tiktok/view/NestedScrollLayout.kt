package com.bytedance.tiktok.view

import android.content.Context
import android.util.AttributeSet
import android.view.VelocityTracker
import android.view.View
import android.view.ViewConfiguration
import android.widget.FrameLayout
import android.widget.Scroller
import androidx.core.view.NestedScrollingParent2
import androidx.core.view.NestedScrollingParentHelper
import androidx.core.view.ViewCompat
import com.bytedance.tiktok.R
import kotlinx.android.synthetic.main.activity_test.view.*

class NestedScrollLayout @JvmOverloads constructor(context: Context,attrs:AttributeSet?=null,defStyleAttr:Int=0)
    : FrameLayout(context,attrs,defStyleAttr),NestedScrollingParent2 {

    private val topBarHeight by lazy { resources.getDimension(R.dimen.top_bar_height) }

    private val contentH by lazy {resources.getDimension(R.dimen.header_height)}

    private val configuration = ViewConfiguration.get(context)

    private val mTouchSlop = configuration.scaledTouchSlop

    private val mMaximumVelocity = configuration.scaledMaximumFlingVelocity

    private val mMinimumVelocity = configuration.scaledMinimumFlingVelocity

    private var mOverScrollDistance = configuration.scaledOverscrollDistance


    private val upChangeY by lazy {
        resources.getDimension(R.dimen.up_top_bar_change_y)
    }

    private var lastY = 0f

    private val mVelocityTracker by lazy {
        VelocityTracker.obtain()
    }

    private val downStopY by lazy { resources.getDimension(R.dimen.donw_content_alpha_y) }

    private val mParentHelper: NestedScrollingParentHelper by lazy {
        NestedScrollingParentHelper(this)
    }

    private val mScroll by lazy {
        Scroller(context)
    }


    override fun getNestedScrollAxes(): Int {
        return mParentHelper.nestedScrollAxes
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

    override fun onNestedFling(target: View, velocityX: Float, velocityY: Float, consumed: Boolean): Boolean {
        return false
    }

    override fun onNestedPreScroll(target: View, dx: Int, dy: Int, consumed: IntArray, type: Int) {
        val translationY = scroll_content.translationY - dy
        val headerY = header.translationY - dy
        //处理上滑
        if (dy > 0) {
            if (translationY >= upChangeY) {
                consumeTranslationY(header, headerY, scroll_content, translationY, dy, consumed)
            } else {
                consumeTranslationY(header, headerY - (translationY - upChangeY),
                        scroll_content, upChangeY, (scroll_content.translationY - upChangeY).toInt(), consumed)
            }
        }
        //处理下滑
        if (dy < 0 && !target.canScrollVertically(-1)) {

            //下滑时处理Fling,完全折叠时，下滑Recycler(或NestedScrollView) Fling滚动到列表顶部（或视图顶部）停止Fling
            if (type == ViewCompat.TYPE_NON_TOUCH && content.y == topBarHeight) {
                return
            }

            if (translationY in upChangeY..downStopY) {
                consumeTranslationY(header, headerY, scroll_content, translationY, dy, consumed)
            } else {
                consumeTranslationY(header, header.translationY + (downStopY - scroll_content.translationY),
                        scroll_content, downStopY, (downStopY - scroll_content.translationY).toInt(), consumed)
            }
        }
    }

    override fun onNestedScroll(target: View, dxConsumed: Int, dyConsumed: Int, dxUnconsumed: Int, dyUnconsumed: Int, type: Int) {

    }

    override fun onStopNestedScroll(target: View, type: Int) {

    }

    private fun consumeTranslationY(parent :View,parentY: Float,content: View,contentY:Float,consumed: Int,dyConsumed: IntArray) {
        parent.translationY = parentY
        content.translationY = contentY
        dyConsumed[1] = consumed
    }

}