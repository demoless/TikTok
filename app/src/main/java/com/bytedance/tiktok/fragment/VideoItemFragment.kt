package com.bytedance.tiktok.fragment

import android.content.res.AssetFileDescriptor
import android.media.MediaPlayer
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.*
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
        private val mediaPlayer: MediaPlayer = MediaPlayer()) :Fragment(),MediaPlayer.OnPreparedListener {

    private val fileDescriptor: AssetFileDescriptor? by lazy {
        context?.resources?.openRawResourceFd(videoBean.videoRes)
    }

    private val coverDrawableRes by lazy {
        context?.resources?.getDrawable(videoBean.coverRes,null)
    }

    private var isPrepared: Boolean = false

    private val holder: SurfaceHolder by lazy {
        surface_view.holder
    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_recommend_item,container,false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        Log.e("message","VideoItemFragment: onViewCreated")
        tv_nickname.text = videoBean.userBean.nickName
        tv_content.text = videoBean.content
        mediaPlayer.reset()
        mediaPlayer.setOnPreparedListener(this)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            surface_view.foreground = coverDrawableRes
        }
    }

    override fun onResume() {
        super.onResume()
        Log.e("message","VideoItemFragment: onResume")
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
        Log.e("message","VideoItemFragment: onPause")
        mediaPlayer.pause()
    }

    override fun onStop() {
        super.onStop()
        Log.e("message","VideoItemFragment: onStop")
        mediaPlayer.stop()
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.e("message","VideoItemFragment: onDestroy")
        mediaPlayer.stop()
        mediaPlayer.release()
    }

    override fun onPrepared(mp: MediaPlayer?) {
        Log.e("message","VideoItemFragment: onPrepared")
        isPrepared = true
        mediaPlayer.setDisplay(holder)
        start()
    }

    private fun start() {
        if (isPrepared && !mediaPlayer.isPlaying) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                surface_view.foreground = null
            }
            mediaPlayer.start()
        }
    }
}