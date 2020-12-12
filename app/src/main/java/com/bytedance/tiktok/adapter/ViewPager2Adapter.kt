package com.bytedance.tiktok.adapter

import android.util.Log
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.adapter.FragmentViewHolder
import com.bytedance.tiktok.bean.DataCreate
import com.bytedance.tiktok.bean.VideoBean
import com.bytedance.tiktok.fragment.VideoItemFragment

open class ViewPager2Adapter(fragmentActivity: FragmentActivity)
    :FragmentStateAdapter(fragmentActivity) {

    private var datas: ArrayList<Fragment> = ArrayList<Fragment>()

    private var videoData: List<VideoBean> = ArrayList<VideoBean>()

    constructor(fragmentActivity: FragmentActivity, list: List<VideoBean>) : this(fragmentActivity) {
        this.videoData = list
        videoData.takeIf { it.isNotEmpty() }?.forEachIndexed { index, _ ->
            datas.add(createFragment(index))
        }
    }

    constructor(fragmentActivity: FragmentActivity,datas: ArrayList<Fragment>): this(fragmentActivity) {
        this.datas = datas
    }

    override fun getItemCount(): Int {
        return datas.size
    }

    override fun onBindViewHolder(holder: FragmentViewHolder, position: Int, payloads: MutableList<Any>) {
        super.onBindViewHolder(holder, position, payloads)
    }

    override fun createFragment(position: Int): Fragment {
        Log.d("msg","ViewPager2Adapter createFragment--position=${position}")
        return VideoItemFragment(DataCreate.datas[position])
    }

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        Log.d("msg","ViewPager2Adapter onAttachedToRecyclerView")
    }

    override fun onDetachedFromRecyclerView(recyclerView: RecyclerView) {
        super.onDetachedFromRecyclerView(recyclerView)
        Log.d("msg","ViewPager2Adapter onAttachedToRecyclerView")
    }

    override fun onViewDetachedFromWindow(holder: FragmentViewHolder) {
        super.onViewDetachedFromWindow(holder)
        Log.d("msg","ViewPager2Adapter onAttachedToRecyclerView")
    }

}