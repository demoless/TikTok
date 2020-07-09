package com.bytedance.tiktok.view.behavior

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.View
import android.view.ViewGroup.LayoutParams
import android.widget.OverScroller
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.view.ViewCompat
import androidx.core.widget.NestedScrollView
import androidx.recyclerview.widget.RecyclerView
import com.bytedance.tiktok.R
import java.lang.reflect.Field


class ContentScrollBehavior @JvmOverloads constructor(context: Context,attributeSet: AttributeSet)
    : CoordinatorLayout.Behavior<View>(context,attributeSet) {

    private lateinit var content : View

    private var flingFromCollaps = false

    //滑动内容初始位置
    private val contentY by lazy { context.resources.getDimension(R.dimen.donw_content_alpha_y)  }

    //上滑改变topBar临界值
    private val topBarY by lazy { context.resources.getDimension(R.dimen.up_top_bar_change_y) }

    //topBar高度
    private val topBarH by lazy { context.resources.getDimension(R.dimen.top_bar_height) }

    private val downEndY by lazy { context.resources.getDimension(R.dimen.donw_content_alpha_y)}

    override fun onMeasureChild(parent: CoordinatorLayout, child: View, parentWidthMeasureSpec: Int,
                                widthUsed: Int, parentHeightMeasureSpec: Int, heightUsed: Int): Boolean {

        val childLayoutParamHeight = child.layoutParams.height
        if (childLayoutParamHeight == LayoutParams.MATCH_PARENT
                || childLayoutParamHeight ==  LayoutParams.WRAP_CONTENT) {
            var availableHeight = View.MeasureSpec.getSize(parentHeightMeasureSpec)
            if (availableHeight == 0) {
                availableHeight = parent.height
            }
            val heightMeasureSpec = View.MeasureSpec.makeMeasureSpec(availableHeight,
                    if (childLayoutParamHeight == LayoutParams.MATCH_PARENT)  View.MeasureSpec.EXACTLY  else View.MeasureSpec.AT_MOST)
            parent.onMeasureChild(child,parentWidthMeasureSpec, widthUsed, heightMeasureSpec, heightUsed)
            return true
        }
        return false
        //return super.onMeasureChild(parent, child, parentWidthMeasureSpec, widthUsed, parentHeightMeasureSpec, heightUsed)
    }

    override fun onLayoutChild(parent: CoordinatorLayout, child: View, layoutDirection: Int): Boolean {
        val handleLayout =  super.onLayoutChild(parent, child, layoutDirection)
        content = child
        return handleLayout
    }

    //NestedScrollParent
    override fun onStartNestedScroll(coordinatorLayout: CoordinatorLayout, child: View,
                                     directTargetChild: View, target: View, axes: Int): Boolean {

        return onStartNestedScroll(coordinatorLayout, child, directTargetChild, target, axes, ViewCompat.TYPE_TOUCH)
    }

    override fun onNestedScrollAccepted(coordinatorLayout: CoordinatorLayout, child: View,
                                        directTargetChild: View, target: View, axes: Int) {
        onNestedScrollAccepted(coordinatorLayout, child, directTargetChild, target, axes,ViewCompat.TYPE_TOUCH)
    }

    override fun onNestedPreScroll(coordinatorLayout: CoordinatorLayout, child: View, target: View,
                                   dx: Int, dy: Int, consumed: IntArray) {
        onNestedPreScroll(coordinatorLayout, child, target, dx, dy, consumed,ViewCompat.TYPE_TOUCH)
    }

    override fun onStopNestedScroll(coordinatorLayout: CoordinatorLayout, child: View, target: View) {
        onStopNestedScroll(coordinatorLayout, child, target,ViewCompat.TYPE_TOUCH)
    }

    override fun onNestedPreFling(coordinatorLayout: CoordinatorLayout, child: View, target: View,
                                  velocityX: Float, velocityY: Float): Boolean {
        flingFromCollaps =  child.y <= contentY
        return false
    }

    override fun onNestedScrollAccepted(coordinatorLayout: CoordinatorLayout, child: View,
                                        directTargetChild: View, target: View, axes: Int, type: Int) {

    }

    override fun onStopNestedScroll(coordinatorLayout: CoordinatorLayout, child: View, target: View, type: Int) {

    }

    override fun onStartNestedScroll(coordinatorLayout: CoordinatorLayout, child: View, directTargetChild: View, target: View, axes: Int, type: Int): Boolean {
        return child.id == R.id.scroll_content && axes == ViewCompat.SCROLL_AXIS_VERTICAL
    }

    override fun onNestedPreScroll(coordinatorLayout: CoordinatorLayout, child: View, target: View,
                                   dx: Int, dy: Int, consumed: IntArray, type: Int) {
        Log.e("message","child.y:" + child.y)
        val transY = child.y - dy

        //处理上滑
        if (dy > 0) {
            if (transY >= topBarY) {
                consumeTranslationY(child,transY,dy,consumed)
            } else {
                val consY = child.y - topBarY
                consumeTranslationY(child,topBarY,consY.toInt(),consumed)
            }
        }

        if (dy < 0  && !target.canScrollVertically(-1)) {
            if (type == ViewCompat.TYPE_NON_TOUCH&&transY >= contentY&&flingFromCollaps) {
                flingFromCollaps=false
                consumeTranslationY(child, contentY,dy, consumed)
                stopViewScroll(target)
                return
            }

            //真正处理下滑
            if (transY >= topBarY && transY <= downEndY) {
                consumeTranslationY(child,transY, dy, consumed)
            } else {
                val consY = downEndY - child.y
                consumeTranslationY(child,downEndY, consY.toInt(), consumed)
            }
        }

    }

    private fun stopViewScroll(target: View) {
        if (target is RecyclerView) {
            target.stopScroll()
        }
        if (target is NestedScrollView) {
            try {
                val clazz: Class<out NestedScrollView> = target.javaClass
                val mScroller: Field = clazz.getDeclaredField("mScroller")
                mScroller.setAccessible(true)
                val overScroller = mScroller.get(target) as OverScroller
                overScroller.abortAnimation()
            } catch (e: NoSuchFieldException) {
                e.printStackTrace()
            } catch (e: IllegalAccessException) {
                e.printStackTrace()
            }
        }
    }

    private fun consumeTranslationY(target: View,transY: Float,dy: Int,consumed: IntArray) {
        target.y = transY
        consumed[1] = dy
        Log.e("message","child.y:" + target.y)
    }

}