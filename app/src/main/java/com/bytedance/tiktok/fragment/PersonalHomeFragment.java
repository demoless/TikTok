package com.bytedance.tiktok.fragment;

import android.content.Intent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.app.ActivityOptionsCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.viewpager.widget.ViewPager;

import com.androidkun.xtablayout.XTabLayout;
import com.bytedance.tiktok.R;
import com.bytedance.tiktok.activity.FocusActivity;
import com.bytedance.tiktok.activity.ShowImageActivity;
import com.bytedance.tiktok.base.BaseFragment;
import com.bytedance.tiktok.base.CommPagerAdapter;
import com.bytedance.tiktok.bean.VideoBean;
import com.bytedance.tiktok.utils.NumUtils;
import com.bytedance.tiktok.view.CircleImageView;
import com.bytedance.tiktok.view.IconFontTextView;
import com.bytedance.tiktok.viewmodels.MainViewModel;
import com.google.android.material.appbar.AppBarLayout;

import java.util.ArrayList;

import butterknife.BindView;


public class PersonalHomeFragment extends BaseFragment implements View.OnClickListener {
    @BindView(R.id.iv_bg)
    ImageView ivBg;
    @BindView(R.id.iv_head)
    CircleImageView ivHead;
    @BindView(R.id.ll_focus)
    LinearLayout llFocus;
    @BindView(R.id.ll_fans)
    LinearLayout llFans;
    @BindView(R.id.iv_return)
    ImageView ivReturn;
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.tv_focus)
    TextView tvFocus;
    @BindView(R.id.iv_more)
    IconFontTextView ivMore;

    @BindView(R.id.tablayout)
    XTabLayout tabLayout;
    @BindView(R.id.appbarlayout)
    AppBarLayout appBarLayout;
    @BindView(R.id.viewpager)
    ViewPager viewPager;
    @BindView(R.id.tv_nickname)
    TextView tvNickname;
    @BindView(R.id.tv_sign)
    TextView tvSign;
    @BindView(R.id.tv_getlike_count)
    TextView tvGetLikeCount;
    @BindView(R.id.tv_focus_count)
    TextView tvFocusCount;
    @BindView(R.id.tv_fans_count)
    TextView tvFansCount;
    @BindView(R.id.tv_addfocus)
    TextView tvAddfocus;
    private ArrayList<Fragment> fragments = new ArrayList<>();
    private CommPagerAdapter pagerAdapter;
    private VideoBean.UserBean curUserBean;
    private MainViewModel mainViewModel;

    @Override
    protected int setLayoutId() {
        return R.layout.fragment_personal_home;
    }

    @Override
    protected void init() {

        mainViewModel = ViewModelProviders.of(getActivity()).get(MainViewModel.class);
        setAppbarLayoutPercent();

        ivReturn.setOnClickListener(this);
        ivHead.setOnClickListener(this);
        ivBg.setOnClickListener(this);
        llFocus.setOnClickListener(this);
        llFans.setOnClickListener(this);

        setUserInfo();
    }

    /**
     * 过渡动画跳转页面
     *
     * @param view
     */
    public void transitionAnim(View view, int res) {
        ActivityOptionsCompat compat = ActivityOptionsCompat.makeSceneTransitionAnimation(getActivity(), view, getString(R.string.trans));
        Intent intent = new Intent(getActivity(), ShowImageActivity.class);
        intent.putExtra("res", res);
        ActivityCompat.startActivity(getActivity(), intent, compat.toBundle());
    }

    public void setUserInfo() {

        mainViewModel.getCurUserEvent().observe(this, curUserBean -> {
                coordinatorLayoutBackTop();

                PersonalHomeFragment.this.curUserBean = curUserBean.getUserBean();

                ivBg.setImageResource(curUserBean.getUserBean().getHead());
                ivHead.setImageResource(curUserBean.getUserBean().getHead());
                tvNickname.setText(curUserBean.getUserBean().getNickName());
                tvSign.setText(curUserBean.getUserBean().getSign());
                tvTitle.setText(curUserBean.getUserBean().getNickName());

                String subCount = NumUtils.numberFilter(curUserBean.getUserBean().getSubCount());
                String focusCount = NumUtils.numberFilter(curUserBean.getUserBean().getFocusCount());
                String fansCount = NumUtils.numberFilter(curUserBean.getUserBean().getFansCount());

                //获赞 关注 粉丝
                tvGetLikeCount.setText(subCount);
                tvFocusCount.setText(focusCount);
                tvFansCount.setText(fansCount);

                //关注状态
                if (curUserBean.getUserBean().isFocused()) {
                    tvAddfocus.setText("已关注");
                    tvAddfocus.setBackgroundResource(R.drawable.shape_round_halfwhite);
                } else {
                    tvAddfocus.setText("关注");
                    tvAddfocus.setBackgroundResource(R.drawable.shape_round_red);
                }

                setTabLayout();
            }
        );
    }

    private void setTabLayout() {
        String[] titles = new String[]{"作品 " + curUserBean.getWorkCount(), "动态 " + curUserBean.getDynamicCount(), "喜欢 " + curUserBean.getLikeCount()};

        fragments.clear();
        for (int i = 0; i < titles.length; i++) {
            fragments.add(new WorkFragment());
            tabLayout.addTab(tabLayout.newTab().setText(titles[i]));
        }

        pagerAdapter = new CommPagerAdapter(getChildFragmentManager(), fragments, titles);
        viewPager.setAdapter(pagerAdapter);
        tabLayout.setupWithViewPager(viewPager);
    }

    /**
     * 根据滚动比例渐变view
     */
    private void setAppbarLayoutPercent() {
        appBarLayout.addOnOffsetChangedListener((appBarLayout, verticalOffset) -> {
            float percent = (Math.abs(verticalOffset * 1.0f) / appBarLayout.getTotalScrollRange());  //滑动比例

            if (percent > 0.8) {
                tvTitle.setVisibility(View.VISIBLE);
                tvFocus.setVisibility(View.VISIBLE);

                float alpha = 1 - (1 - percent) * 5;  //渐变变换
                tvTitle.setAlpha(alpha);
                tvFocus.setAlpha(alpha);
            } else {
                tvTitle.setVisibility(View.GONE);
                tvFocus.setVisibility(View.GONE);
            }
        });
    }

    /**
     * 自动回顶部
     */
    private void coordinatorLayoutBackTop() {
        CoordinatorLayout.Behavior behavior =
                ((CoordinatorLayout.LayoutParams) appBarLayout.getLayoutParams()).getBehavior();
        if (behavior instanceof AppBarLayout.Behavior) {
            AppBarLayout.Behavior appBarLayoutBehavior = (AppBarLayout.Behavior) behavior;
            int topAndBottomOffset = appBarLayoutBehavior.getTopAndBottomOffset();
            if (topAndBottomOffset != 0) {
                appBarLayoutBehavior.setTopAndBottomOffset(0);
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_return:
                mainViewModel.getPageChangeEvent().postValue(0);
                break;
            case R.id.iv_head:
                transitionAnim(ivHead, curUserBean.getHead());
                break;
            case R.id.ll_focus:
            case R.id.ll_fans:
                startActivity(new Intent(getActivity(), FocusActivity.class));
                break;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (fragments != null && !fragments.isEmpty()) {
            fragments.clear();
        }
    }
}
