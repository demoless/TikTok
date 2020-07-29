package com.bytedance.tiktok.adapter

import android.media.MediaPlayer
import android.util.Log
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.adapter.FragmentViewHolder
import com.bytedance.tiktok.bean.DataCreate
import com.bytedance.tiktok.fragment.VideoItemFragment

/**
 * created by demoless on 2020/7/5
 * description:
 */
class VideoListAdapter constructor(fragmentActivity: FragmentActivity)
    : ViewPager2Adapter(fragmentActivity) {

    private var mediaPlayer:MediaPlayer? = null

    constructor(fragmentActivity: FragmentActivity,mediaPlayer: MediaPlayer) :this(fragmentActivity) {
        this.mediaPlayer = mediaPlayer
    }

    override fun getItemCount(): Int {
        return DataCreate.datas.size
    }

    override fun createFragment(position: Int): Fragment {
        mediaPlayer?.let {
            return VideoItemFragment(DataCreate.datas[position],it)
        }
        return VideoItemFragment(DataCreate.datas[position])
    }

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        Log.e("message","VideoListAdapter:onAttachedToRecyclerView")
//        mediaPlayer?.apply {
//            prepareAsync()
//        }
    }

    override fun onViewDetachedFromWindow(holder: FragmentViewHolder) {
        super.onViewDetachedFromWindow(holder)
        Log.e("message","VideoListAdapter:onViewDetachedFromWindow")
        mediaPlayer?.apply {
            if (this.isPlaying) {
                stop()
            }
            release()
        }
    }
}