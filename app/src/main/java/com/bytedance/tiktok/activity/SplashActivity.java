package com.bytedance.tiktok.activity;

import android.content.Intent;

import com.bytedance.tiktok.R;
import com.bytedance.tiktok.base.BaseActivity;

import java.util.concurrent.TimeUnit;

import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.core.SingleObserver;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

/**
 * create on 2020/5/19
 * description App启动页面
 */
public class SplashActivity extends BaseActivity {

    @Override
    protected int setLayoutId() {
        return R.layout.activity_splash;
    }

    @Override
    protected void init() {
        setFullScreen();
        Single.timer(500, TimeUnit.MILLISECONDS).subscribeOn(Schedulers.io()).subscribe(new SingleObserver<Long>() {
            @Override
            public void onSubscribe(@NonNull Disposable d) {

            }

            @Override
            public void onSuccess(@NonNull Long aLong) {
                startActivity(new Intent(SplashActivity.this, MainKtActivity.class));
                finish();
            }

            @Override
            public void onError(@NonNull Throwable e) {
                e.printStackTrace();
            }
        });
    }
}
