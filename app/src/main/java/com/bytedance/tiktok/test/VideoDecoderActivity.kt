package com.bytedance.tiktok.test

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.app.PendingIntent.getActivity
import android.content.res.AssetFileDescriptor
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bytedance.tiktok.R
import com.bytedance.tiktok.bean.DataCreate
import com.bytedance.tiktok.decode.AudioDecoder
import com.bytedance.tiktok.decode.VideoDecoder
import kotlinx.android.synthetic.main.activity_video_decoder.*
import java.util.concurrent.Executors

class VideoDecoderActivity : AppCompatActivity() {

//    val scaleXAnimator by lazy {
//        ObjectAnimator.ofFloat(test_view, "scaleX", 1f,0.8f, 1f).apply {
//            this.duration = 830
//        }
//    }
//
//    val scaleYAnimator by lazy {
//        ObjectAnimator.ofFloat(test_view, "scaleY", 1f,0.8f, 1f).apply {
//            this.duration = 830
//        }
//    }
//
//    val scaleYAnimate by lazy {
//        ObjectAnimator.ofFloat(test_view, "innerR", 1f, 1.2f).apply {
//            this.duration = 666
//        }
//    }
//
//    val strokeWidthAnimate by lazy {
//        ObjectAnimator.ofFloat(test_view, "strokeWidth", 1f, 1f).apply {
//            this.duration = 666
//        }
//    }
//
//    val outSideAlpha by lazy {
//        ObjectAnimator.ofFloat(test_view, "outSideAlpha", 1f, 0.3f, 1f).apply {
//            this.duration = 708
//        }
//    }
//
//    val set by lazy {
//        AnimatorSet().apply {
//            play(scaleXAnimator).with(scaleYAnimate).with(strokeWidthAnimate).with(outSideAlpha).with(scaleYAnimator)
//            this.duration = 830
//        }
//    }

        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            setContentView(R.layout.activity_video_decoder)
            initPlayer()
            frameInfo.text = ffmpegInfo()
            frameInfo.setOnClickListener {
                test_view.startAnim()
            }
        }

    override fun onResume() {
        super.onResume()
    }
        private fun initPlayer() {
            val path = "android.resource://"+packageName + "/" + R.raw.video1

            //创建线程池
            val threadPool = Executors.newFixedThreadPool(2)

            //创建视频解码器
            val videoDecoder = VideoDecoder(this,path, sfv, null)
            threadPool.execute(videoDecoder)

            //创建音频解码器
            val audioDecoder = AudioDecoder(this,path)
            threadPool.execute(audioDecoder)

            //开启播放
            videoDecoder.start()
            audioDecoder.start()
        }

        private external fun ffmpegInfo(): String

        companion object {
        init {
            System.loadLibrary("native-lib")
        }
    }

}