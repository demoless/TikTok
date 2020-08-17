package com.bytedance.tiktok.test

import android.annotation.SuppressLint
import android.content.res.AssetFileDescriptor
import android.media.MediaPlayer
import android.os.Bundle
import android.view.SurfaceHolder
import androidx.appcompat.app.AppCompatActivity
import com.bytedance.tiktok.R
import kotlinx.android.synthetic.main.activity_empty_video.*


class EmptyVideoActivity : AppCompatActivity() {

    private val mediaPlayer by lazy {
        MediaPlayer().apply {
            this.isLooping = true
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_empty_video)
    }

    override fun onResume() {
        super.onResume()
        surface_view.holder.addCallback(object:SurfaceHolder.Callback{
            override fun surfaceChanged(holder: SurfaceHolder?, format: Int, width: Int, height: Int) {

            }

            override fun surfaceDestroyed(holder: SurfaceHolder?) {

            }

            @SuppressLint("ResourceType")
            override fun surfaceCreated(holder: SurfaceHolder?) {
                val fileDescriptor: AssetFileDescriptor = resources.openRawResourceFd(2131623936)
                mediaPlayer.setDataSource(fileDescriptor.fileDescriptor ,fileDescriptor.startOffset,
                        fileDescriptor.length)
                mediaPlayer.prepareAsync()
                mediaPlayer.setOnPreparedListener{
                    it.setDisplay(holder)
                    it.start()
                }
            }

        })
    }
}