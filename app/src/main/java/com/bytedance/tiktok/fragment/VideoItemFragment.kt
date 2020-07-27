package com.bytedance.tiktok.fragment

import android.content.res.AssetFileDescriptor
import android.media.MediaPlayer
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.SurfaceHolder
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.bytedance.tiktok.R
import com.bytedance.tiktok.bean.DataCreate
import com.bytedance.tiktok.bean.VideoBean
import kotlinx.android.synthetic.main.fragment_recommend_item.*
import kotlinx.android.synthetic.main.layout_recommend_text.*

/**
 * created by demoless on 2020/7/5
 * description:
 */
class VideoItemFragment @JvmOverloads constructor(
        private val videoBean: VideoBean,
        private val mediaPlayer: MediaPlayer = MediaPlayer()) :Fragment() {

    val fileDescriptor: AssetFileDescriptor = resources.openRawResourceFd(videoBean.videoRes)

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_recommend_item,container,false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        tv_nickname.text = videoBean.userBean.nickName
        tv_content.text = videoBean.content
        surface_view.holder.addCallback(object :SurfaceHolder.Callback{
            override fun surfaceChanged(holder: SurfaceHolder?, format: Int, width: Int, height: Int) {

            }

            override fun surfaceDestroyed(holder: SurfaceHolder?) {

            }

            override fun surfaceCreated(holder: SurfaceHolder?) {
                mediaPlayer.reset()
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

    override fun onResume() {
        super.onResume()
        Log.e("message", "onResume")
    }

    override fun onPause() {
        super.onPause()
        mediaPlayer.pause()
    }

    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer.stop()
        mediaPlayer.release()
    }
}