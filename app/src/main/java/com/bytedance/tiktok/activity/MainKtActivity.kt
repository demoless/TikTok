package com.bytedance.tiktok.activity

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.os.*
import android.util.Log
import android.view.*
import android.widget.TextView
import android.widget.Toast
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
import com.google.zxing.BarcodeFormat
import com.google.zxing.EncodeHintType
import com.google.zxing.WriterException
import com.google.zxing.common.BitMatrix
import com.google.zxing.qrcode.QRCodeWriter
import kotlinx.android.synthetic.main.activity_main_kt.*
import okhttp3.*
import java.io.IOException
import java.util.*
import kotlin.collections.ArrayList

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
        view_pager_home.adapter = Adapter(this, fragments)
        view_pager_home.isUserInputEnabled = false
        showFloatWindow()
        getLoginMsg()
        login_qc.setOnClickListener { login_qc.visibility = View.GONE }
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
        val view = LayoutInflater.from(this).inflate(R.layout.layout_float_window, null)
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

    private val loginInfoListener: ILoginInfoListener by lazy {
        object : ILoginInfoListener {
            override fun onLoginInfoSucess(data: RQData) {
                runOnUiThread {
                    login_qc.visibility = View.GONE
                }
            }

            override fun onLoginInfoFail(msg: String) {
                runOnUiThread {
                    login_qc.visibility = View.GONE
                    Toast.makeText(this@MainKtActivity,"登陆失败",Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private val okHttpClient: OkHttpClient by lazy {
        OkHttpClient()
    }

    private fun getLoginMsg() {
        val url = "https://passport.bilibili.com/x/passport-tv-login/qrcode/auth_code"
        val requestBody = FormBody.Builder()
                .add("appkey", "4409e2ce8ffd12b8")
                .add("local_id", "0")
                .add("ts", "0")
                .add("sign", "e134154ed6add881d28fbdf68653cd9c")
                .build()
        val request = Request.Builder().url(url).post(requestBody).build()
        val call = okHttpClient.newCall(request)
        call.enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {

            }

            override fun onResponse(call: Call, response: Response) {
                val gson = Gson()
                val result = gson.fromJson<LoginInfo<Data>>(response.body?.string(), LoginInfo::class.java)
                createQRImage(result.data.url)
            }

        })
    }

    val QR_WIDTH = 120
    val QR_HEIGHT = 120
    fun createQRImage(url: String?) {
        try {
            //判断URL合法性
            if (url == null || "" == url || url.length < 1) {
                return
            }
            val hints: Hashtable<EncodeHintType, String> = Hashtable<EncodeHintType, String>()
            hints.put(EncodeHintType.CHARACTER_SET, "utf-8")
            //图像数据转换，使用了矩阵转换
            val bitMatrix: BitMatrix = QRCodeWriter().encode(url, BarcodeFormat.QR_CODE, QR_WIDTH, QR_HEIGHT, hints)
            val pixels = IntArray(QR_WIDTH * QR_HEIGHT)
            //下面这里按照二维码的算法，逐个生成二维码的图片，
            //两个for循环是图片横列扫描的结果
            for (y in 0 until QR_HEIGHT) {
                for (x in 0 until QR_WIDTH) {
                    if (bitMatrix.get(x, y)) {
                        pixels[y * QR_WIDTH + x] = -0x1000000
                    } else {
                        pixels[y * QR_WIDTH + x] = -0x1
                    }
                }
            }
            //生成二维码图片的格式，使用ARGB_8888
            val bitmap: Bitmap = Bitmap.createBitmap(QR_WIDTH, QR_HEIGHT, Bitmap.Config.ARGB_8888)
            bitmap.setPixels(pixels, 0, QR_WIDTH, 0, 0, QR_WIDTH, QR_HEIGHT)
            //显示到一个ImageView上面
            runOnUiThread {
                login_qc.visibility = View.VISIBLE
                login_qc.setImageBitmap(bitmap)
            }
            getLoginResult()
        } catch (e: WriterException) {
            e.printStackTrace()
        }
    }

    val RETRY_GET_INFO = -1
    val SUCESS_GET_INFO = 1

    private fun getLoginResult() {
        val handlerThread = HandlerThread("login-result-check")
        handlerThread.start()
        val handler = RetryHandler(handlerThread.looper,loginInfoListener)
        handler.sendEmptyMessage(RETRY_GET_INFO)
    }

    interface ILoginInfoListener {
        fun onLoginInfoSucess(data: RQData)

        fun onLoginInfoFail(msg: String)
    }

    class RetryHandler(looper: Looper,private val listener: ILoginInfoListener): Handler(looper) {
        companion object {
            const val RETRY_GET_INFO = -1
            const val SUCESS_GET_INFO = 1
        }
        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)
            when(msg.what) {
                RETRY_GET_INFO -> {
                    getLoginResultByNet()
                }
            }
        }

        private fun getLoginResultByNet() {
            val url = "https://passport.bilibili.com/x/passport-tv-login/qrcode/poll"
            val okHttpClient = OkHttpClient()
            val requestBody = FormBody.Builder()
                    .add("appkey", "4409e2ce8ffd12b8")
                    .add("auth_code","6214464b3025541abf6f654cf7569a01")
                    .add("local_id", "0")
                    .add("ts", "0")
                    .add("sign", "e134154ed6add881d28fbdf68653cd9c")
                    .build()
            val request = Request.Builder().url(url).post(requestBody).build()
            val call = okHttpClient.newCall(request)
            call.enqueue(object : okhttp3.Callback {
                override fun onFailure(call: Call, e: IOException) {
                    sendEmptyMessage(RETRY_GET_INFO)
                }

                override fun onResponse(call: Call, response: Response) {
                    val gson = Gson()
                    val result = gson.fromJson<LoginInfo<RQData>>(response.body?.string(), LoginInfo::class.java)
                    when(result.code) {
                        0 -> {
                            listener.onLoginInfoSucess(result.data)
                        }
                        //API校验密匙错误 请求错误 二维码已失效
                        -3, -400,86038 -> {
                            listener.onLoginInfoFail(result.message)
                        }
                        86039 -> {
                            sendEmptyMessageDelayed(RETRY_GET_INFO,1000)
                        }
                    }
                }

            })
        }
    }

    data class LoginInfo<T>(@SerializedName("code") val code: Int,
                         @SerializedName("message") val message: String,
                         @SerializedName("ttl") val ttl: Int,
                         @SerializedName("data") val data: T) {
        override fun toString(): String {
            return "code:$code" +
                    "message:$message" +
                    "ttl:$ttl" +
                    "data:"+data.toString()
        }
    }

    data class Data(@SerializedName("url") val url: String, @SerializedName("auth_code") val auth_code: String) {
        override fun toString(): String {
            return "url:$url" +
                    "auth_code:$auth_code"
        }
    }

    data class RQData(@SerializedName("mid") val mid: Long,
                      @SerializedName("access_token") val accessToken: String,
                      @SerializedName("refresh_token") val refashToken: String) {
        override fun toString(): String {
            return "mid:$mid\t"+
                    "access_token$accessToken\t"+
                    "refresh_token$refashToken\t"
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
                    shiftMode.setBoolean(tabView, false)
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