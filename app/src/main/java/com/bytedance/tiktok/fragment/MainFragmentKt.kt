package com.bytedance.tiktok.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.viewpager.widget.ViewPager
import com.bytedance.tiktok.R
import com.bytedance.tiktok.base.CommPagerAdapter
import com.bytedance.tiktok.viewmodels.MainViewModel
import kotlinx.android.synthetic.main.fragment_main.*

class MainFragmentKt : Fragment() {

    companion object {
        /** 默认显示第一页推荐页  */
        @JvmStatic var CUR_PAGE = 1
    }

    private val mainViewModel : MainViewModel by lazy {
        ViewModelProviders.of(this).get(MainViewModel::class.java)
    }
    private var pagerAdapter: CommPagerAdapter? = null
    private val currentLocationFragment by lazy{
        CurrentLocationFragment()
    }
    private val recommendFragment by lazy {
        RecommendFragment();
    }
    private val fragments by lazy { ArrayList<Fragment>() }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_main,container,false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        setFragments()
    }

    private fun setFragments() {
        fragments.add(currentLocationFragment)
        fragments.add(recommendFragment)
        tab_title.addTab(tab_title.newTab().setText("重庆"))
        tab_title.addTab(tab_title.newTab().setText("推荐"))
        pagerAdapter = CommPagerAdapter(childFragmentManager, fragments, arrayOf("重庆", "推荐"))
        viewpager.adapter = pagerAdapter
        tab_title.setupWithViewPager(viewpager)
        tab_title.getTabAt(1)?.select()
        viewpager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {}

            override fun onPageSelected(position: Int) {
                CUR_PAGE = position
                if (position == 1) {
                    //继续播放
                    mainViewModel.state.postValue(true)
                } else {
                    //切换到其他页面，需要暂停视频
                    mainViewModel.state.postValue(false)
                }
            }
            override fun onPageScrollStateChanged(state: Int) {}
        })
    }
}