package com.bytedance.tiktok.test

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.bytedance.tiktok.R
import com.bytedance.tiktok.adapter.VideoListAdapter
import com.bytedance.tiktok.base.CommPagerAdapter
import com.bytedance.tiktok.fragment.ViewPagerFragment
import com.bytedance.tiktok.fragment.WorkFragment
import kotlinx.android.synthetic.main.activity_test.*
import kotlinx.android.synthetic.main.view_pager2.*

class TestActivity : AppCompatActivity() {

    private lateinit var pagerAdapter:CommPagerAdapter


    private val fragments by lazy {
        ArrayList<Fragment>().apply {
            for (index in 0 .. 10) {
                this.add( ViewPagerFragment())
            }
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.view_pager2)
    }

    override fun onResume() {
        super.onResume()
        //setTabLayout()
        viewpager2.adapter = VideoListAdapter(this)
    }

    private fun setTabLayout() {
        val titles = arrayOf("作品 ", "动态 ", "喜欢 ")
        fragments.clear()
        for (i in titles.indices) {
            fragments.add(WorkFragment())
            Toast.makeText(this,""+i,Toast.LENGTH_SHORT).show()
            xt_tab_layout.addTab(xt_tab_layout.newTab().setText(titles[i]))
        }
        pagerAdapter = CommPagerAdapter(supportFragmentManager, fragments, titles)
        view_pager.adapter = pagerAdapter
        xt_tab_layout.setupWithViewPager(view_pager)
    }
}