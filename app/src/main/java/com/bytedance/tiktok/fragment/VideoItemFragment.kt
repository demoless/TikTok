package com.bytedance.tiktok.fragment

import android.annotation.SuppressLint
import android.content.res.AssetFileDescriptor
import android.graphics.Color
import android.media.MediaPlayer
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.SurfaceHolder
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import com.bytedance.tiktok.R
import com.bytedance.tiktok.bean.VideoBean
import com.bytedance.tiktok.viewmodels.MainFragmentViewModel
import kotlinx.android.synthetic.main.fragment_recommend_item.*
import kotlinx.android.synthetic.main.layout_recommend_text.*

/**
 * created by demoless on 2020/7/5
 * description:
 */
@SuppressLint("UseCompatLoadingForDrawables")
class VideoItemFragment constructor(private val videoBean: VideoBean)
    :Fragment(),MediaPlayer.OnPreparedListener, SurfaceHolder.Callback2, IVideoController {

    private val mainFragmentViewModel : MainFragmentViewModel by lazy {
        ViewModelProviders.of(this)[MainFragmentViewModel::class.java]
    }

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
        surface_view.holder.addCallback(this)
        mediaPlayer.reset()
        mediaPlayer.setOnPreparedListener(this)
        if (!isPrepared) {
            fileDescriptor?.let {
                Log.e("message","setDataSource")
                mediaPlayer.setDataSource(it.fileDescriptor ,it.startOffset,
                        it.length)
            }
            mediaPlayer.prepareAsync()
        }
    }

    override fun onResume() {
        super.onResume()
        Log.e("message","VideoItemFragment${videoBean.videoRes} : onResume")
        if (isPrepared && !mediaPlayer.isPlaying) {
            startVideo()
        }
    }

    override fun onPause() {
        super.onPause()
        Log.e("message","VideoItemFragment${videoBean.videoRes}: onPause")
        pauseVideo()
    }

    override fun onStop() {
        super.onStop()
        Log.e("message","VideoItemFragment${videoBean.videoRes}: onStop")
        stopVideo()
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.e("message","VideoItemFragment${videoBean.videoRes}: onDestroy")
        onStop()
        mediaPlayer.release()
    }

    override fun onPrepared(mp: MediaPlayer?) {
        Log.e("message","VideoItemFragment${videoBean.videoRes}: onPrepared")
        isPrepared = true
        surface_view.visibility = View.VISIBLE
        mediaPlayer.setDisplay(surface_view.holder)
        startVideo()
        video_background.setBackgroundColor(Color.TRANSPARENT)
    }

    override fun startVideo() {
        if (isPrepared && !mediaPlayer.isPlaying) {
            mediaPlayer.start()
        }
        mainFragmentViewModel.videoStatus.value = true
    }

    override fun stopVideo() {
        if (mediaPlayer.isPlaying) {
            mediaPlayer.stop()
        }
        mainFragmentViewModel.videoStatus.value = false
    }

    override fun pauseVideo() {
        if (mediaPlayer.isPlaying) {
            mediaPlayer.start()
        }
        mainFragmentViewModel.videoStatus.value = false
    }

    override fun surfaceCreated(holder: SurfaceHolder?) {
        Log.e("message","VideoItemFragment${videoBean.videoRes}: surfaceCreated")
    }

    override fun surfaceChanged(holder: SurfaceHolder?, format: Int, width: Int, height: Int) {
        mediaPlayer.setDisplay(holder)
        Log.e("message","VideoItemFragment${videoBean.videoRes}: surfaceChanged")
    }

    override fun surfaceDestroyed(holder: SurfaceHolder?) {
        if (mediaPlayer.isPlaying) {
            mediaPlayer.pause()
        }
        Log.e("message","VideoItemFragment${videoBean.videoRes}: surfaceDestroyed")
    }

    override fun surfaceRedrawNeeded(holder: SurfaceHolder?) {
        Log.e("message","VideoItemFragment${videoBean.videoRes}: surfaceRedrawNeeded")
    }

    override fun surfaceCreated(holder: SurfaceHolder?) {
        Log.e("message","VideoItemFragment${videoBean.videoRes}: surfaceCreated")
    }

    override fun surfaceChanged(holder: SurfaceHolder?, format: Int, width: Int, height: Int) {
        mediaPlayer.setDisplay(holder)
        Log.e("message","VideoItemFragment${videoBean.videoRes}: surfaceChanged")
    }

    override fun surfaceDestroyed(holder: SurfaceHolder?) {
        if (mediaPlayer.isPlaying) {
            mediaPlayer.pause()
        }
        Log.e("message","VideoItemFragment${videoBean.videoRes}: surfaceDestroyed")
    }

    override fun surfaceRedrawNeeded(holder: SurfaceHolder?) {
        Log.e("message","VideoItemFragment${videoBean.videoRes}: surfaceRedrawNeeded")
    }
}