package com.bytedance.tiktok.view.behavior

import android.content.Context
import android.util.AttributeSet
import android.view.View
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.view.ViewCompat
import com.bytedance.tiktok.R

class HeaderScrollBehavior @JvmOverloads constructor(context: Context,attributeSet: AttributeSet)
    :CoordinatorLayout.Behavior<View>(context,attributeSet) {
    //topBar高度
    private val topBarH by lazy { context.resources.getDimension(R.dimen.top_bar_height) }

    //是否是折叠状态
    private var isFlingFromCollaps = false

    //NestedScrollParent
    override fun onStartNestedScroll(coordinatorLayout: CoordinatorLayout, child: View,
                                     directTargetChild: View, target: View, axes: Int): Boolean {
        return onStartNestedScroll(coordinatorLayout, child, directTargetChild, target, axes, ViewCompat.TYPE_TOUCH)
    }

    override fun onNestedScrollAccepted(coordinatorLayout: CoordinatorLayout, child: View,
                                        directTargetChild: View, target: View, axes: Int) {
        onNestedScrollAccepted(coordinatorLayout, child, directTargetChild, target, axes, ViewCompat.TYPE_TOUCH)
    }

    override fun onNestedPreScroll(coordinatorLayout: CoordinatorLayout, child: View, target: View,
                                   dx: Int, dy: Int, consumed: IntArray) {
        onNestedPreScroll(coordinatorLayout, child, target, dx, dy, consumed, ViewCompat.TYPE_TOUCH)
    }

    override fun onStopNestedScroll(coordinatorLayout: CoordinatorLayout, child: View, target: View) {
        onStopNestedScroll(coordinatorLayout, child, target, ViewCompat.TYPE_TOUCH)
    }

    override fun onStartNestedScroll(coordinatorLayout: CoordinatorLayout, child: View, directTargetChild: View,
                                     target: View, axes: Int, type: Int): Boolean {
        return child.id == R.id.behavior_header && axes == ViewCompat.SCROLL_AXIS_VERTICAL
    }

    override fun onNestedPreFling(coordinatorLayout: CoordinatorLayout, child: View, target: View, velocityX: Float, velocityY: Float): Boolean {
        isFlingFromCollaps = child.y < 0
        return false
    }
}