package com.bytedance.tiktok.adapter

import android.util.Log
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.adapter.FragmentViewHolder
import com.bytedance.tiktok.bean.VideoBean
import com.bytedance.tiktok.fragment.VideoItemFragment
import kotlin.collections.ArrayList

/**
 * created by demoless on 2020/7/5
 * description:
 */
class VideoListAdapter(fragmentManager: FragmentManager,private val data: ArrayList<VideoBean>) : FragmentStateAdapter(fragmentManager) {

    override fun getItem(position: Int): Fragment {
        Log.e("--mess","VideoListAdapter")
        return VideoItemFragment(position,data[position])
    }

    override fun getItemCount(): Int {
        return data.size
    }
}