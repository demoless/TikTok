package com.bytedance.tiktok.view.floatView

import android.animation.TimeInterpolator

class CircleAlphaInterpolator: TimeInterpolator {
    companion object {
        private const val v1 = 830.toFloat().div(41)
        private const val v2 = 830.toFloat().div(667)
        private const val input1 = 41.toFloat().div(830)
        private const val input2 = 667.toFloat().div(830)
    }
    override fun getInterpolation(input: Float): Float {
        return if (input <= input1) {
            v1 * input
        } else if (input <= input2) {
            1 - v2 * input
        } else {
            0f
        }
    }
}