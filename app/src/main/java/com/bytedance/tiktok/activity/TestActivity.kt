package com.bytedance.tiktok.activity

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.bytedance.tiktok.R
import com.bytedance.tiktok.base.CommPagerAdapter
import com.bytedance.tiktok.fragment.WorkFragment
import kotlinx.android.synthetic.main.activity_test.*

class TestActivity : AppCompatActivity() {

    private lateinit var pagerAdapter:CommPagerAdapter


    private val fragments by lazy {
        ArrayList<Fragment>()
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_test)
    }

    override fun onResume() {
        super.onResume()
        setTabLayout()
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