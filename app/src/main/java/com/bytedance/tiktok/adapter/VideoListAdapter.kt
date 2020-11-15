package com.bytedance.tiktok.adapter

import android.media.MediaPlayer
import android.util.Log
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.adapter.FragmentViewHolder
import com.bytedance.tiktok.bean.DataCreate
import com.bytedance.tiktok.fragment.VideoItemFragment

/**
 * created by demoless on 2020/7/5
 * description:
 */
class VideoListAdapter constructor(fragment: Fragment)
    : FragmentStateAdapter(fragment) {


    override fun getItemCount(): Int {
        return DataCreate.datas.size
    }

    @ExperimentalStdlibApi
    override fun createFragment(position: Int): VideoItemFragment {
        return VideoItemFragment(DataCreate.datas[position])
    }

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        Log.e("message","VideoListAdapter:onAttachedToRecyclerView")
    }

    override fun onViewDetachedFromWindow(holder: FragmentViewHolder) {
        super.onViewDetachedFromWindow(holder)
        Log.e("message","VideoListAdapter:onViewDetachedFromWindow")
    }

    data class Node constructor(var next: Node?, val mediaPlayer: MediaPlayer, var flag: Int)
}