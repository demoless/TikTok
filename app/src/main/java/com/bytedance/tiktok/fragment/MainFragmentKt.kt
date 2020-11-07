package com.bytedance.tiktok.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModelProviders
import androidx.viewpager.widget.ViewPager
import com.bytedance.tiktok.R
import com.bytedance.tiktok.base.CommPagerAdapter
import com.bytedance.tiktok.viewmodels.MainActivityViewModel
import com.bytedance.tiktok.viewmodels.MainFragmentViewModel
import kotlinx.android.synthetic.main.fragment_main.*

class MainFragmentKt : Fragment() {

    companion object {
        /** 默认显示第一页推荐页  */
        @JvmStatic var CUR_PAGE = 1
    }

    private val mainActivityViewModel : MainActivityViewModel by lazy {
        ViewModelProviders.of(context as FragmentActivity).get(MainActivityViewModel::class.java)
    }

    private val mainFragmentViewModel : MainFragmentViewModel by lazy {
        ViewModelProviders.of(this)[MainFragmentViewModel::class.java]
    }
    private var pagerAdapter: CommPagerAdapter? = null
    private val currentLocationFragment by lazy{
        CurrentLocationFragmentKt()
    }
    private val recommendFragment by lazy {
        RecommendFragment()
    }

    private val recommendFragmentKt by lazy{
        RecommendFragmentKt()
    }
    private val fragments by lazy {
        ArrayList<Fragment>().apply {
            this.add(currentLocationFragment)
            this.add(recommendFragmentKt)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_main,container,false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setFragments()
    }

    private fun setFragments() {
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
                mainFragmentViewModel.currPageEvent.postValue(position)
                mainActivityViewModel.state.postValue(position == 1)
            }
            override fun onPageScrollStateChanged(state: Int) {}
        })
    }
}