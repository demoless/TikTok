package com.bytedance.tiktok.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class MainFragmentViewModel : ViewModel() {
    //首页viewpager位置 0同城 1推荐
    val currPageEvent by lazy { MutableLiveData<Int>().apply { this.value = 1 } }
}