package com.bytedance.tiktok.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter

open class ViewPager2Adapter(fragmentActivity: FragmentActivity)
    :FragmentStateAdapter(fragmentActivity) {

    private var datas: ArrayList<Fragment> = ArrayList<Fragment>()

    constructor(fragmentActivity: FragmentActivity,datas: ArrayList<Fragment>): this(fragmentActivity) {
        this.datas = datas
    }
    override fun getItemCount(): Int {
        return datas.size
    }

    override fun createFragment(position: Int): Fragment {
        return if (itemCount == 0) Fragment() else datas[position]
    }
}