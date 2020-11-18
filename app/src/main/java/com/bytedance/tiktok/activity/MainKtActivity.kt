package com.bytedance.tiktok.activity

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.WindowManager
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.bytedance.tiktok.R
import com.bytedance.tiktok.fragment.MainFragmentKt
import com.bytedance.tiktok.performence.FpsMonitor
import com.google.android.material.bottomnavigation.BottomNavigationItemView
import com.google.android.material.bottomnavigation.BottomNavigationMenuView
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import kotlinx.android.synthetic.main.activity_main_kt.*
import okhttp3.*
import java.io.IOException

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
        view_pager_home.registerOnPageChangeCallback(pageChangeListener)
        bottom_navigation.setOnNavigationItemSelectedListener(navigationItemSelectedListener)
        view_pager_home.adapter = Adapter(this,fragments)
        view_pager_home.isUserInputEnabled = false
        showFloatWindow()
        getLoginMsg()
    }

    @SuppressLint("InflateParams", "RtlHardcoded")
    private fun showFloatWindow() {
        val windowManager = getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val windowParams = WindowManager.LayoutParams().apply {
            gravity = Gravity.TOP or Gravity.LEFT
            x = 0
            y = 50
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
            }
            flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL or
            WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN or WindowManager.LayoutParams.FLAG_LAYOUT_INSET_DECOR or
            WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH
            width = WindowManager.LayoutParams.WRAP_CONTENT
            height = WindowManager.LayoutParams.WRAP_CONTENT
        }
        val view = LayoutInflater.from(this).inflate(R.layout.layout_float_window,null)
        windowManager.addView(view, windowParams)
        FpsMonitor.startMonitor { fps ->
            Log.d("fps", String.format("fps: %s", fps))
            view.findViewById<TextView>(R.id.tv_fps).text = String.format("fps: %s", fps)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        FpsMonitor.stopMonitor()
    }

    private fun getLoginMsg() {
        val url = "https://passport.bilibili.com/x/passport-tv-login/qrcode/auth_code"
        val okHttpClient = OkHttpClient()
        val requestBody = FormBody.Builder()
                .add("appkey","4409e2ce8ffd12b8")
                .add("local_id","0")
                .add("ts","0")
                .add("sign","e134154ed6add881d28fbdf68653cd9c")
                .build()
        val request = Request.Builder().url(url).post(requestBody).build()
        val call = okHttpClient.newCall(request)
        call.enqueue(object: Callback {
            override fun onFailure(call: Call, e: IOException) {

            }

            override fun onResponse(call: Call, response: Response) {
                val gson = Gson()
                val result = gson.fromJson<LoginInfo>(response.body?.string(),LoginInfo::class.java)
            }

        })
    }

    data class LoginInfo(@SerializedName("code") val  code: Int,
                         @SerializedName("message") val message: String,
                         @SerializedName("ttl") val ttl: Int,
                         @SerializedName("data") val data: Data) {
        override fun toString(): String {
            return "code:$code" +
                    "message:$message" +
                    "ttl:$ttl" +
                    "data:"+data.toString()
        }
    }

    data class Data(@SerializedName("url") val url:String,@SerializedName("auth_code") val auth_code:String) {
        override fun toString(): String {
            return "url:$url" +
                    "auth_code:$auth_code"
        }
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