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
import com.bytedance.tiktok.R
import com.bytedance.tiktok.adapter.VideoListAdapter
import com.bytedance.tiktok.bean.VideoBean
import kotlinx.android.synthetic.main.fragment_recommend_item.*
import kotlinx.android.synthetic.main.layout_recommend_text.*

/**
 * created by demoless on 2020/7/5
 * description:
 */
@SuppressLint("UseCompatLoadingForDrawables")
class VideoItemFragment constructor(
        private val videoBean: VideoBean)
    :Fragment(), MediaPlayer.OnPreparedListener, SurfaceHolder.Callback2, IVideoController, View.OnClickListener {

    private val fileDescriptor: AssetFileDescriptor? by lazy {
        context?.resources?.openRawResourceFd(videoBean.videoRes)
    }

    private val mediaPlayer: MediaPlayer by lazy {
        MediaPlayerProvider.obtainMediaPlayer()
    }

    private val coverDrawableRes by lazy {
        context?.resources?.getDrawable(videoBean.coverRes,null)
    }

    val position = tag

    private var isPrepared: Boolean = false

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        Log.e("message","VideoItemFragment${videoBean.videoRes}: onCreateView")
        return inflater.inflate(R.layout.fragment_recommend_item,container,false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        tv_nickname.text = videoBean.userBean.nickName
        tv_content.text = videoBean.content
        video_background.background = coverDrawableRes
        surface_view.holder.addCallback(this)
        video_background.setOnClickListener(this)
        if (!isPrepared) {
            fileDescriptor?.let {
                Log.e("message","setDataSource")
                mediaPlayer.setDataSource(it.fileDescriptor ,it.startOffset,
                        it.length)
            }
            mediaPlayer.prepareAsync()
            mediaPlayer.setOnPreparedListener(this)
        }
    }

    override fun onResume() {
        super.onResume()
        Log.e("message","VideoItemFragment${videoBean.videoRes} : onResume")
        startVideo()
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

    override fun onDestroyView() {
        super.onDestroyView()
        mediaPlayer.reset()
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.e("message","VideoItemFragment${videoBean.videoRes}: onDestroy")
        mediaPlayer.release()
        /*val node = VideoListAdapter.Node(null,mediaPlayer,MediaPlayerProvider.FLAG_STATS_FREE)
        MediaPlayerProvider.recycleNode(node)*/
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
    }

    override fun stopVideo() {
        if (mediaPlayer.isPlaying) {
            mediaPlayer.stop()
        }
    }

    override fun pauseVideo() {
        if (mediaPlayer.isPlaying) {
            mediaPlayer.pause()
        }
    }

    override fun surfaceCreated(holder: SurfaceHolder?) {
        Log.e("message","VideoItemFragment${videoBean.videoRes}: surfaceCreated")
        mediaPlayer.setDisplay(holder)
    }

    override fun surfaceChanged(holder: SurfaceHolder?, format: Int, width: Int, height: Int) {
        mediaPlayer.setDisplay(holder)
        Log.e("message","VideoItemFragment${videoBean.videoRes}: surfaceChanged")
    }

    override fun surfaceDestroyed(holder: SurfaceHolder?) {
        pauseVideo()
        Log.e("message","VideoItemFragment${videoBean.videoRes}: surfaceDestroyed")
    }

    override fun surfaceRedrawNeeded(holder: SurfaceHolder?) {
        Log.e("message","VideoItemFragment${videoBean.videoRes}: surfaceRedrawNeeded")
    }

    override fun onClick(v: View?) {
        if (mediaPlayer.isPlaying) {
            pauseVideo()
            iv_play.visibility = View.VISIBLE
        } else {
            startVideo()
            iv_play.visibility = View.GONE
        }
    }

    class MediaPlayerProvider {
        companion object {
            private const val TAG = "VideoListAdapter"
            const val FLAG_STATS_FREE = 0
            private const val FLAG_STATS_IN_USE = 1
            @JvmStatic
            private var mediaPlayerPool: VideoListAdapter.Node? = null

            @JvmStatic
            private var currSize = 0

            @JvmStatic
            fun obtainMediaPlayer(): MediaPlayer {
                if (currSize == 0) {
                    mediaPlayerPool = VideoListAdapter.Node(null, logNewMediaPlayer(), FLAG_STATS_FREE)
                    currSize++
                }
                if (currSize > 0 && mediaPlayerPool != null) {
                    val mediaPlayer = mediaPlayerPool!!
                    mediaPlayerPool = mediaPlayer.next
                    currSize--
                    mediaPlayer.flag = FLAG_STATS_IN_USE
                    return mediaPlayer.mediaPlayer
                }
                return logNewMediaPlayer()
            }

            @JvmStatic
            fun recycleNode(itemNode: VideoListAdapter.Node) {
                itemNode.next = mediaPlayerPool
                mediaPlayerPool = itemNode
                currSize++
            }

            @JvmStatic
            private fun logNewMediaPlayer(): MediaPlayer {
                Log.d(TAG,"new MediaPlayer====== currSize${currSize+1}")
                return MediaPlayer()
            }
        }
    }
}