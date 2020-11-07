package com.bytedance.tiktok.fragment;

import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.bytedance.tiktok.R;
import com.bytedance.tiktok.adapter.GridVideoAdapter;
import com.bytedance.tiktok.base.BaseFragment;
import com.bytedance.tiktok.bean.DataCreate;
import com.bytedance.tiktok.viewmodels.MainFragmentViewModel;

import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

/**
 * create on 2020-05-19
 * description 附近的人fragment
 */
public class CurrentLocationFragment extends BaseFragment {
    @BindView(R.id.recyclerview)
    RecyclerView recyclerView;
    private GridVideoAdapter adapter;

    @BindView(R.id.refreshlayout)
    SwipeRefreshLayout refreshLayout;

    private MainFragmentViewModel viewModel;
    private Disposable disposable;

    @Override
    protected int setLayoutId() {
        return R.layout.fragment_current_location;
    }

    @Override
    protected void init() {
        //new DataCreate().initData();
        viewModel = ViewModelProviders.of((FragmentActivity) getContext()).get(MainFragmentViewModel.class);
        recyclerView.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));

        adapter = new GridVideoAdapter(getActivity(), DataCreate.datas);
        recyclerView.setAdapter(adapter);

        refreshLayout.setColorSchemeResources(R.color.color_link);
        refreshLayout.setOnRefreshListener(() -> {
            disposable = Observable.timer(500, TimeUnit.MILLISECONDS)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(Along ->{
                        refreshLayout.setRefreshing(false);
                    });
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (disposable != null) disposable.dispose();
    }
}
