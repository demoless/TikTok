package com.bytedance.tiktok.fragment

import android.media.MediaPlayer
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.widget.ViewPager2
import com.bytedance.tiktok.R
import com.bytedance.tiktok.adapter.VideoListAdapter
import kotlinx.android.synthetic.main.fragment_recommend_kt.*

class RecommendFragmentKt : Fragment() {

    private val mediaPlayer:MediaPlayer by lazy {
        MediaPlayer().apply {
            this.isLooping = true
        }
    }

    private val adapter by lazy {
        VideoListAdapter(context as FragmentActivity,mediaPlayer)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_recommend_kt,container,false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        recyclerview.registerOnPageChangeCallback(object: ViewPager2.OnPageChangeCallback() {

        })
        recyclerview.adapter = adapter

    }
}