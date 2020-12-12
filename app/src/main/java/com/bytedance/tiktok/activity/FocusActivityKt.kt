package com.bytedance.tiktok.activity

import android.os.Bundle
import android.os.PersistableBundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.bytedance.tiktok.R
import com.bytedance.tiktok.base.CommPagerAdapter
import com.bytedance.tiktok.fragment.FansFragment
import kotlinx.android.synthetic.main.activity_focus.*

class FocusActivityKt: AppCompatActivity() {

    private val fragments by lazy { ArrayList<Fragment>() }
    private val titles = arrayOf("关注 128", "粉丝 128", "推荐关注")
    private lateinit var adapter : CommPagerAdapter

    override fun onCreate(savedInstanceState: Bundle?, persistentState: PersistableBundle?) {
        super.onCreate(savedInstanceState, persistentState)
        setContentView(R.layout.activity_focus)
    }

    override fun onResume() {
        super.onResume()
        for (title in titles) {
            fragments.add(FansFragment())
            tablayout.addTab(tablayout.newTab().setText(title))
        }
        adapter = CommPagerAdapter(supportFragmentManager, fragments, titles)
        viewpager.adapter = adapter
        tablayout.setupWithViewPager(viewpager)
    }

    override fun onDestroy() {
        super.onDestroy()
    }

}