package com.bytedance.tiktok.fragment

import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.bytedance.tiktok.R
import com.bytedance.tiktok.adapter.GridVideoAdapterKt
import com.bytedance.tiktok.base.BaseFragment
import com.bytedance.tiktok.bean.DataCreate
import com.bytedance.tiktok.viewmodels.MainFragmentViewModel
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_current_location.*
import java.util.concurrent.TimeUnit

/**
 * created by demoless on 2020/11/5
 * description:
 */
class CurrentLocationFragmentKt: BaseFragment() {
    private var disposable:Disposable? = null
    private var adapter:GridVideoAdapterKt? = null
    private val viewModel by lazy {
        ViewModelProviders.of(context as FragmentActivity).get(MainFragmentViewModel::class.java)
    }
    override fun init() {
        recyclerview.layoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)

        adapter = activity?.let { GridVideoAdapterKt(it, DataCreate.datas) }
        recyclerview.adapter = adapter

        refreshlayout.setColorSchemeResources(R.color.color_link)
        disposable = Single.timer(500, TimeUnit.MILLISECONDS)
                .subscribeOn(Schedulers.io())
                .subscribe { _: Long? -> refreshlayout.isRefreshing = false }
    }

    override fun setLayoutId(): Int {
        return R.layout.fragment_current_location
    }

    override fun onDestroy() {
        super.onDestroy()
        disposable?.dispose()
    }
}