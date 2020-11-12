package com.bytedance.tiktok.fragment

import android.media.MediaPlayer
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.viewpager2.widget.ViewPager2
import com.bytedance.tiktok.R
import com.bytedance.tiktok.adapter.VideoListAdapter
import com.bytedance.tiktok.viewmodels.MainFragmentViewModel
import kotlinx.android.synthetic.main.fragment_recommend_kt.*

class RecommendFragmentKt : Fragment() {

    companion object {
        private const val TAG = "RecommendFragmentKt"
    }

    private val mediaPlayer:MediaPlayer by lazy {
        MediaPlayer().apply {
            this.isLooping = true
        }
    }

    private val mainFragmentViewModel : MainFragmentViewModel by lazy {
        ViewModelProviders.of(this)[MainFragmentViewModel::class.java]
    }

    private val adapter by lazy {
        VideoListAdapter(context as FragmentActivity,mediaPlayer)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        Log.e(TAG,"RecommendFragmentKt: onCreateView")
        return inflater.inflate(R.layout.fragment_recommend_kt,container,false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        Log.e(TAG,"RecommendFragmentKt: onViewCreated")
        mainFragmentViewModel.videoStatus.observe(viewLifecycleOwner, Observer { })
        recyclerview.registerOnPageChangeCallback(object: ViewPager2.OnPageChangeCallback() {

        })
        recyclerview.adapter = adapter
    }

    override fun onPause() {
        super.onPause()
        Log.e(TAG,"RecommendFragmentKt: onPause")
    }
}