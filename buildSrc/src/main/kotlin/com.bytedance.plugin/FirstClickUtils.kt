package com.bytedance.plugin

class FirstClickUtils {
    companion object {
        private const val FAST_CLICK_TIME_DISTANCE = 300
        private var mLastClickTime: Long = 0L

        fun isDoubleClickHappen(): Boolean{
            val currTime = System.currentTimeMillis()
            val duration = currTime - mLastClickTime
            mLastClickTime = currTime
            return duration <= FAST_CLICK_TIME_DISTANCE
        }
    }
}