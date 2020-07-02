package com.bytedance.tiktok.activity;

import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.viewpager.widget.ViewPager;
import com.bytedance.tiktok.R;
import com.bytedance.tiktok.base.BaseActivity;
import com.bytedance.tiktok.base.CommPagerAdapter;
import com.bytedance.tiktok.bean.MainPageChangeEvent;
import com.bytedance.tiktok.bean.PauseVideoEvent;
import com.bytedance.tiktok.fragment.MainFragment;
import com.bytedance.tiktok.fragment.PersonalHomeFragment;
import com.bytedance.tiktok.fragment.RecommendFragment;
import com.bytedance.tiktok.utils.RxBus;
import com.bytedance.tiktok.viewmodels.MainViewModel;

import java.util.ArrayList;
import butterknife.BindView;
import rx.Subscription;
import rx.functions.Action1;


public class MainActivity extends BaseActivity {
    @BindView(R.id.viewpager)
    ViewPager viewPager;
    private CommPagerAdapter pagerAdapter;
    private ArrayList<Fragment> fragments = new ArrayList<>();
    public static int curMainPage;
    private MainFragment mainFragment = new MainFragment();
    private PersonalHomeFragment personalHomeFragment = new PersonalHomeFragment();
    /** 上次点击返回键时间 */
    private long lastTime;
    /** 连续按返回键退出时间 */
    private final int EXIT_TIME = 2000;
    private MainViewModel mainViewModel;

    @Override
    protected int setLayoutId() {
        return R.layout.activity_main;
    }

    @Override
    protected void init() {
        mainViewModel = ViewModelProviders.of(this).get(MainViewModel.class);
        fragments.add(mainFragment);
        fragments.add(personalHomeFragment);
        pagerAdapter = new CommPagerAdapter(getSupportFragmentManager(), fragments, new String[]{"",""});
        viewPager.setAdapter(pagerAdapter);

        //点击头像切换页面
        mainViewModel.getPageChangeEvent().observe(this, page -> {
            if (viewPager != null) {
                viewPager.setCurrentItem(page);
            }
        });
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                curMainPage = position;

                if (position == 0) {
                    mainViewModel.getState().postValue(true);
                    RxBus.getDefault().post(new PauseVideoEvent(true));
                } else if (position == 1) {
                    mainViewModel.getState().postValue(false);
                    RxBus.getDefault().post(new PauseVideoEvent(false));
                }
            }
            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    @Override
    public void onBackPressed() {
        //双击返回退出App
        if (System.currentTimeMillis() - lastTime > EXIT_TIME) {
            if (viewPager.getCurrentItem() == 1) {
                viewPager.setCurrentItem(0);
            } else {
                Toast.makeText(getApplicationContext(), "再按一次退出", Toast.LENGTH_SHORT).show();
                lastTime = System.currentTimeMillis();
            }
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
