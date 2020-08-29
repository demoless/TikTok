package com.bytedance.tiktok.view.floatView

import android.animation.TimeInterpolator

class ImageViewInterpolator : TimeInterpolator {

    companion object {
        private const val v1 = 66.4.toFloat().div(330)
        private const val v2 = 330.toFloat().div(830)
        private const val v3 = 430.toFloat().div(830)
        private const val v4 = 66.4.toFloat().div(410)
    }

    override fun getInterpolation(input: Float): Float {
        return if (input <= v2) {
            1 - v1 * input
        } else if (input > v2 && input < v3) {
            1 - v1 * v2
        } else {
            (1 - v1 * v2) + (input - v3) * v4
        }
    }
}