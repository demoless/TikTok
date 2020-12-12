package com.bytedance.tiktok.application;

import android.app.Application;
import android.content.Context;

import com.bytedance.boost_multidex.BoostMultiDex;
import com.bytedance.tiktok.bean.DataCreate;


public class App extends Application {
    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        BoostMultiDex.install(base);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        DataCreate.initData();
    }

}
