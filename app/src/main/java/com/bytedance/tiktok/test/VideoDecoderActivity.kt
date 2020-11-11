package com.bytedance.tiktok.test

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.bytedance.tiktok.R
import com.bytedance.tiktok.decode.AudioDecoder
import com.bytedance.tiktok.decode.VideoDecoder
import kotlinx.android.synthetic.main.activity_video_decoder.*
import java.util.concurrent.Executors

class VideoDecoderActivity : AppCompatActivity() {

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
            val videoDecoder = VideoDecoder(this,path)
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