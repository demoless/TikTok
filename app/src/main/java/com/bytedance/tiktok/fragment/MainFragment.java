package com.bytedance.tiktok.fragment;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.viewpager.widget.ViewPager;

import com.androidkun.xtablayout.XTabLayout;
import com.bytedance.tiktok.R;
import com.bytedance.tiktok.base.BaseFragment;
import com.bytedance.tiktok.base.CommPagerAdapter;
import com.bytedance.tiktok.viewmodels.MainViewModel;

import java.util.ArrayList;
import java.util.Objects;

import butterknife.BindView;

public class MainFragment extends BaseFragment {
    private CurrentLocationFragment currentLocationFragment;
    private RecommendFragment recommendFragment;
    @BindView(R.id.viewpager)
    ViewPager viewPager;
    @BindView(R.id.tab_title)
    XTabLayout tabTitle;
    @BindView(R.id.tab_mainmenu)
    XTabLayout tabMainMenu;
    private ArrayList<Fragment> fragments = new ArrayList<>();
    private CommPagerAdapter pagerAdapter;
    private MainViewModel mainViewModel;
    /** 默认显示第一页推荐页 */
    public static int CUR_PAGE = 1;

    @Override
    protected int setLayoutId() {
        return R.layout.fragment_main;
    }

    @Override
    protected void init() {
        mainViewModel = ViewModelProviders.of(getActivity()).get(MainViewModel.class);
        setFragments();

        setMainMenu();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (fragments != null && !fragments.isEmpty())
        fragments.clear();
    }

    private void setFragments() {
        currentLocationFragment = new CurrentLocationFragment();
        recommendFragment = new RecommendFragment();
        fragments.add(currentLocationFragment);
        fragments.add(recommendFragment);

        tabTitle.addTab(tabTitle.newTab().setText("海淀"));
        tabTitle.addTab(tabTitle.newTab().setText("推荐"));

        pagerAdapter = new CommPagerAdapter(getChildFragmentManager(), fragments, new String[] {"海淀", "推荐"});
        viewPager.setAdapter(pagerAdapter);
        tabTitle.setupWithViewPager(viewPager);

        Objects.requireNonNull(tabTitle.getTabAt(1)).select();

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {

                CUR_PAGE = position;

                if (position == 1) {
                    //继续播放
                    mainViewModel.getState().postValue(true);
                } else {
                    //切换到其他页面，需要暂停视频
                    mainViewModel.getState().postValue(false);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    private void setMainMenu() {
        tabMainMenu.addTab(tabMainMenu.newTab().setText("首页"));
        tabMainMenu.addTab(tabMainMenu.newTab().setText("好友"));
        tabMainMenu.addTab(tabMainMenu.newTab().setText(""));
        tabMainMenu.addTab(tabMainMenu.newTab().setText("消息"));
        tabMainMenu.addTab(tabMainMenu.newTab().setText("我"));
    }

}
