package com.bytedance.tiktok.fragment

import android.media.MediaPlayer
import android.os.Bundle
import android.view.*
import android.view.SurfaceHolder.*
import androidx.fragment.app.Fragment
import com.bytedance.tiktok.R
import com.bytedance.tiktok.bean.VideoBean
import kotlinx.android.synthetic.main.fragment_recommend_item.*
import kotlinx.android.synthetic.main.layout_recommend_text.*

/**
 * created by demoless on 2020/7/5
 * description:
 */
class VideoItemFragment(private val videoBean: VideoBean) :Fragment() {

    private var videoPath: String? = null
        set(value) {
            field = value
        }

    constructor(videoPath :String, videoBean: VideoBean) : this(videoBean){
        this.videoPath = videoPath
    }

    private val mediaPlayer:MediaPlayer by lazy {
        MediaPlayer()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mediaPlayer.isLooping = true
        videoPath?.let{
            mediaPlayer.setDataSource(it)
            mediaPlayer.prepare()
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_recommend_item,container,false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        tv_nickname.text = videoBean.userBean.nickName
        tv_content.text = videoBean.content

        val holder = surface_view.holder.also {
            it.addCallback(object : Callback {
                override fun surfaceChanged(holder: SurfaceHolder?, format: Int, width: Int, height: Int) {

                }

                override fun surfaceCreated(holder: SurfaceHolder?) {
                }

                override fun surfaceDestroyed(holder: SurfaceHolder?) {

                }
            })
        }
        mediaPlayer.setDisplay(holder)
    }

    override fun onResume() {
        super.onResume()
        mediaPlayer.start()
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