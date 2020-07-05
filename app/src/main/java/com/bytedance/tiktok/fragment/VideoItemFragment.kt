package com.bytedance.tiktok.fragment

import android.media.MediaPlayer
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.bytedance.tiktok.R
import com.bytedance.tiktok.bean.DataCreate

/**
 * created by demoless on 2020/7/5
 * description:
 */
class VideoItemFragment(position: Int) :Fragment() {
    private val videoPath : String = "android.resource://" + activity?.packageName + "/" + DataCreate.datas[position].videoRes

    private val mediaPlayer:MediaPlayer by lazy {
        MediaPlayer()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mediaPlayer.isLooping = true
        mediaPlayer.setDataSource(videoPath)
        mediaPlayer.prepareAsync()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_recommend_item,container,false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

    }
}