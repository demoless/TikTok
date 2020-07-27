package com.bytedance.tiktok.activity

import android.media.MediaPlayer
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.bytedance.tiktok.R
import com.bytedance.tiktok.adapter.ViewPager2Adapter
import com.bytedance.tiktok.fragment.MainFragmentKt
import com.bytedance.tiktok.fragment.ViewPagerFragment
import com.bytedance.tiktok.utils.TabLayoutMediator
import kotlinx.android.synthetic.main.activity_main_kt.*

class MainKtActivity : AppCompatActivity() {

    private val fragments by lazy{
        ArrayList<Fragment>().apply {
            this.add(MainFragmentKt())
            for (index in 0 ..3) {
                this.add(ViewPagerFragment())
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_kt)
    }

    override fun onResume() {
        super.onResume()
        setMainMenu()
        view_pager_home.adapter = ViewPager2Adapter(this,fragments)
        view_pager_home.isUserInputEnabled = false
        TabLayoutMediator(tab_mainmenu, view_pager_home, TabLayoutMediator.OnConfigureTabCallback { tab, position -> }).attach()
    }

    private fun setMainMenu() {
        tab_mainmenu.addTab(tab_mainmenu.newTab().setText("首页"))
        tab_mainmenu.addTab(tab_mainmenu.newTab().setText("好友"))
        tab_mainmenu.addTab(tab_mainmenu.newTab().setText(""))
        tab_mainmenu.addTab(tab_mainmenu.newTab().setText("消息"))
        tab_mainmenu.addTab(tab_mainmenu.newTab().setText("我"))
    }
}