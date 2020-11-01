package com.bytedance.tiktok.activity

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.bytedance.tiktok.R
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.schedulers.Schedulers
import java.util.concurrent.TimeUnit

/**
 * created by demoless on 2020/11/1
 * description:
 */
class SplashActivityKt() : AppCompatActivity() {
    private var dispose: Disposable ?= null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        init()
    }

    private fun init() {
        dispose = Single.timer(500,TimeUnit.MILLISECONDS)
                .subscribeOn(Schedulers.io())
                .subscribe { _ ->
                    startActivity(Intent(this, MainKtActivity::class.java))
                    finish()
                }
    }

    override fun onDestroy() {
        super.onDestroy()
        dispose?.dispose()
    }
}