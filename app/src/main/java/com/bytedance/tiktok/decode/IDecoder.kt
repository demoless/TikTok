package com.bytedance.tiktok.decode

import android.media.MediaFormat

interface IDecoder :Runnable {
    //开始解码
    fun start()

    //暂停解码
    fun pause()

    //继续解码
    fun reStart()

    //停止解码
    fun stop()

    //是否正在解码
    fun isDecoding() : Boolean

    fun getWidth() : Int

    fun getHeight() : Int

    fun getDuration() : Long

    fun getMediaFormat() :MediaFormat?

    fun getTrack() : Int

    fun getFilePath() :String

    fun setStateListener(l: IDecoderStateListener?)

    fun getCurTimeStamp(): Long
    fun seekTo(pos: Long): Long
    fun seekAndPlay(pos: Long): Long
    fun isStop(): Boolean
    fun isSeeking(): Boolean
    fun withoutSync(): IDecoder
    fun getRotationAngle(): Int
}