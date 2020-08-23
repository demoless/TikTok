package com.bytedance.tiktok.test

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
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_video_decoder)
        initPlayer()
        frameInfo.text = ffmpegInfo()
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