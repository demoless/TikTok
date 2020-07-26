package com.bytedance.tiktok.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.adapter.FragmentViewHolder
import com.bytedance.tiktok.bean.DataCreate
import com.bytedance.tiktok.fragment.VideoItemFragment

/**
 * created by demoless on 2020/7/5
 * description:
 */
class VideoListAdapter constructor(private val fragmentActivity: FragmentActivity)
    : FragmentStateAdapter(fragmentActivity) {

    override fun getItemCount(): Int {
        return DataCreate.datas.size
    }

    override fun onBindViewHolder(holder: FragmentViewHolder, position: Int, payloads: MutableList<Any>) {
        super.onBindViewHolder(holder, position, payloads)
    }

    override fun createFragment(position: Int): Fragment {
        val bgVideoPath = "android.resource://" + fragmentActivity.packageName + "/" + DataCreate.datas[position].videoRes
        return VideoItemFragment(bgVideoPath,DataCreate.datas[position])
    }

    override fun onViewDetachedFromWindow(holder: FragmentViewHolder) {
        super.onViewDetachedFromWindow(holder)
    }
}