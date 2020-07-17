package com.bytedance.tiktok.adapter

import android.util.Log
import androidx.fragment.app.FragmentManager
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.adapter.FragmentViewHolder
import com.bytedance.tiktok.fragment.VideoItemFragment
import kotlin.collections.ArrayList

/**
 * created by demoless on 2020/7/5
 * description:
 */
class VideoListAdapter(fragmentManager: FragmentManager,private val data: ArrayList<VideoItemFragment>) : FragmentStateAdapter(fragmentManager) {

    override fun getItem(position: Int): VideoItemFragment {
        Log.e("--mess","VideoListAdapter")
        return data[position]
    }

    override fun getItemCount(): Int {
        return data.size
    }

    override fun onBindViewHolder(holder: FragmentViewHolder, position: Int, payloads: MutableList<Any>) {
        super.onBindViewHolder(holder, position, payloads)
    }

    override fun onViewDetachedFromWindow(holder: FragmentViewHolder) {
        super.onViewDetachedFromWindow(holder)
    }
}