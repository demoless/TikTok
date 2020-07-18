package com.bytedance.tiktok.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import com.bytedance.tiktok.R
import com.bytedance.tiktok.adapter.VideoListAdapter
import com.bytedance.tiktok.bean.DataCreate
import kotlinx.android.synthetic.main.fragment_recommend_kt.*

class RecommendFragmentKt : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_recommend_kt,container,false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        view_pager2.registerOnPageChangeCallback(object: ViewPager2.OnPageChangeCallback() {
            override fun onPageScrollStateChanged(state: Int) {

            }

            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {

            }

            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
            }
        })
        view_pager2.adapter = fragmentManager?.let { VideoListAdapter(it,DataCreate.videoFragments) }
    }
}