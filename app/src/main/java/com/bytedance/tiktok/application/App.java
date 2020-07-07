package com.bytedance.tiktok.application;

import android.app.Application;

import com.bytedance.tiktok.bean.DataCreate;


public class App extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        DataCreate.initData();
    }

}
