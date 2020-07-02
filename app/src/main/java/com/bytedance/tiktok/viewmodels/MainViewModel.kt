package com.bytedance.tiktok.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class MainViewModel : ViewModel() {
    public val state : MutableLiveData<Boolean> by lazy {
        MutableLiveData<Boolean>().apply{
            this.value = true
        }
    }

    public val pageChangeEvent : MutableLiveData<Int> by lazy {
        MutableLiveData<Int>().apply {
            this.value = 0;
        }
    }
}

