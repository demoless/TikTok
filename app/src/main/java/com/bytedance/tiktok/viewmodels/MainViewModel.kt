package com.bytedance.tiktok.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.bytedance.tiktok.bean.CurUserBean
import com.bytedance.tiktok.bean.DataCreate

class MainViewModel : ViewModel() {
    val state by lazy {
        MutableLiveData<Boolean>().apply{
            this.value = true
        }
    }

    val pageChangeEvent by lazy {
        MutableLiveData<Int>().apply {
            this.value = 0
        }
    }

    val curUserEvent by lazy {MutableLiveData<CurUserBean>().apply { CurUserBean(DataCreate.datas[0].userBean) }}
}

