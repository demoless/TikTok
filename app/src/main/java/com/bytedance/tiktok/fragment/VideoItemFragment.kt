package com.bytedance.tiktok.fragment

import android.annotation.SuppressLint
import android.content.res.AssetFileDescriptor
import android.graphics.Color
import android.media.MediaPlayer
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.bytedance.tiktok.R
import com.bytedance.tiktok.bean.VideoBean
import kotlinx.android.synthetic.main.fragment_recommend_item.*
import kotlinx.android.synthetic.main.layout_recommend_text.*

/**
 * created by demoless on 2020/7/5
 * description:
 */
@SuppressLint("UseCompatLoadingForDrawables")
class VideoItemFragment constructor(
        private val videoBean: VideoBean) :Fragment(),MediaPlayer.OnPreparedListener {

    private val fileDescriptor: AssetFileDescriptor? by lazy {
        context?.resources?.openRawResourceFd(videoBean.videoRes)
    }

    private val coverDrawableRes by lazy {
        context?.resources?.getDrawable(videoBean.coverRes,null)
    }

    private val mediaPlayer: MediaPlayer by lazy{
        MediaPlayer.create(context,videoBean.videoRes)
    }

    private var isPrepared: Boolean = false

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        Log.e("message","VideoItemFragment${videoBean.videoRes}: onCreateView")
        return inflater.inflate(R.layout.fragment_recommend_item,container,false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        Log.e("message","VideoItemFragment${videoBean.videoRes}: onViewCreated")
        tv_nickname.text = videoBean.userBean.nickName
        tv_content.text = videoBean.content
        video_background.background = coverDrawableRes
        mediaPlayer.reset()
        mediaPlayer.setOnPreparedListener(this)
    }

    override fun onResume() {
        super.onResume()
        Log.e("message","VideoItemFragment${videoBean.videoRes} : onResume")
        if (!isPrepared) {
            fileDescriptor?.let {
                Log.e("message","setDataSource")
                mediaPlayer.setDataSource(it.fileDescriptor ,it.startOffset,
                        it.length)
            }
            mediaPlayer.prepareAsync()
        }

        start()
    }

    override fun onPause() {
        super.onPause()
        Log.e("message","VideoItemFragment${videoBean.videoRes}: onPause")
        if(mediaPlayer.isPlaying) {
            mediaPlayer.pause()
        }
    }

    override fun onStop() {
        super.onStop()
        Log.e("message","VideoItemFragment${videoBean.videoRes}: onStop")
        mediaPlayer.stop()
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.e("message","VideoItemFragment${videoBean.videoRes}: onDestroy")
        if (mediaPlayer.isPlaying) {
            mediaPlayer.stop()
        }
        mediaPlayer.release()
    }

    override fun onPrepared(mp: MediaPlayer?) {
        Log.e("message","VideoItemFragment${videoBean.videoRes}: onPrepared")
        isPrepared = true
        surface_view.visibility = View.VISIBLE
        mediaPlayer.setDisplay(surface_view.holder)
        start()
        video_background.setBackgroundColor(Color.TRANSPARENT)
    }

    private fun start() {
        if (isPrepared && !mediaPlayer.isPlaying) {
            mediaPlayer.start()
        }
    }
}