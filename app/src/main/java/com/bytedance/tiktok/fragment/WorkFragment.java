package com.bytedance.tiktok.fragment;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bytedance.tiktok.R;
import com.bytedance.tiktok.adapter.WorkAdapter;
import com.bytedance.tiktok.base.BaseFragment;
import com.bytedance.tiktok.bean.DataCreate;

import butterknife.BindView;

/**
 * create on 2020-05-19
 * description 个人作品fragment
 */
public class WorkFragment extends BaseFragment {
    @BindView(R.id.recyclerview)
    RecyclerView recyclerView;
    private WorkAdapter workAdapter;
    private RecyclerView.LayoutManager gridlayoutManager;

    @Override
    protected int setLayoutId() {
        return R.layout.fragment_work;
    }

    @Override
    protected void init() {
        gridlayoutManager = new GridLayoutManager(getContext(), 3);
        recyclerView.setLayoutManager(gridlayoutManager);
        workAdapter = new WorkAdapter(getContext(), DataCreate.datas);
        recyclerView.setAdapter(workAdapter);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        recyclerView.setLayoutManager(null);
    }
}
