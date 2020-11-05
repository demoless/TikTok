package com.bytedance.tiktok.activity

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.bytedance.tiktok.R
import com.bytedance.tiktok.fragment.MainFragmentKt
import com.google.android.material.bottomnavigation.BottomNavigationItemView
import com.google.android.material.bottomnavigation.BottomNavigationMenuView
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.android.synthetic.main.activity_main_kt.*

class MainKtActivity : AppCompatActivity() {

    private var menuItem: MenuItem? = null

    private val fragments by lazy{
        ArrayList<Fragment>().apply {
            this.add(MainFragmentKt())
            for (index in 0 ..3) {
                this.add(Fragment())
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_kt)
    }

    override fun onResume() {
        super.onResume()
        BottomNavigationViewHelper.disableShiftMode(bottom_navigation)
        view_pager_home.registerOnPageChangeCallback(pageChangeListener)
        bottom_navigation.setOnNavigationItemSelectedListener(navigationItemSelectedListener)
        view_pager_home.adapter = Adapter(this,fragments)
        view_pager_home.isUserInputEnabled = false
    }

    private val pageChangeListener = object: ViewPager2.OnPageChangeCallback() {
        override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
            if (menuItem == null) {
                bottom_navigation.menu.getItem(0).isChecked = false
            }
            menuItem?.isChecked = false
            menuItem = bottom_navigation.menu.getItem(position)
            menuItem?.isChecked = true
        }

    }

    private val navigationItemSelectedListener by lazy {
        BottomNavigationView.OnNavigationItemSelectedListener {
            when(it.itemId) {
                R.id.item_news ->
                    view_pager_home.currentItem = 0
                R.id.item_lib ->
                    view_pager_home.currentItem = 1
                R.id.item_find ->
                    view_pager_home.currentItem = 2
                R.id.item_more ->
                    view_pager_home.currentItem = 3
            }
            return@OnNavigationItemSelectedListener false
        }
    }

    class Adapter(fragmentActivity: FragmentActivity, private val fragments: List<Fragment>): FragmentStateAdapter(fragmentActivity) {

        override fun getItemCount(): Int {
            return fragments.size
        }

        override fun createFragment(position: Int): Fragment {
            return fragments[position]
        }

    }

    class BottomNavigationViewHelper {
        companion object {
            @SuppressLint("RestrictedApi")
            @JvmStatic
            fun disableShiftMode(navigationView: BottomNavigationView) {
                val tabView = navigationView.getChildAt(0) as BottomNavigationMenuView
                try {
                    val shiftMode = tabView.javaClass.getDeclaredField("mShiftingMode")
                    shiftMode.isAccessible = true
                    shiftMode.setBoolean(tabView,false)
                    shiftMode.isAccessible = false
                    for (index in 0 ..tabView.childCount) {
                        val itemView = tabView.getChildAt(index) as BottomNavigationItemView
                        itemView.setShifting(false)
                        itemView.setChecked(itemView.itemData.isChecked)
                    }
                }catch (e: Exception) {
                    e.printStackTrace();
                }
            }
        }
    }
}