package com.bytedance.tiktok.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.adapter.FragmentViewHolder
import kotlin.collections.ArrayList

/**
 * created by demoless on 2020/7/5
 * description:
 */
class VideoListAdapter(fragmentManager: FragmentManager) : FragmentStateAdapter(fragmentManager) {
    private lateinit var data: ArrayList<Fragment>

    constructor(fragmentManager: FragmentManager,data: ArrayList<Fragment>) : this(fragmentManager) {
        this.data = data
    }

    fun setData(data: ArrayList<Fragment>) {
        this.data = data
        notifyDataSetChanged()
    }

    override fun onBindViewHolder(holder: FragmentViewHolder, position: Int, payloads: MutableList<Any>) {
        super.onBindViewHolder(holder, position, payloads)
    }


    override fun onViewDetachedFromWindow(holder: FragmentViewHolder) {
        super.onViewDetachedFromWindow(holder)
    }


    override fun getItem(position: Int): Fragment {
        return data[position]
    }

    override fun getItemCount(): Int {
        return data.size
    }
}