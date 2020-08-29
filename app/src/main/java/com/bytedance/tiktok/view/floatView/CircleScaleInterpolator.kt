package com.bytedance.tiktok.view.floatView

import android.animation.TimeInterpolator

class CircleScaleInterpolator: TimeInterpolator {
    companion object {
        private const val v = 166.toFloat().div(666)
        private const val lastResult = 1.2f
        private const val input1 = 666.toFloat().div(830)
    }
    override fun getInterpolation(input: Float): Float {
        return if (input < input1) {
            1 + v * input
        } else {
            lastResult
        }
    }
}