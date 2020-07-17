package com.bytedance.tiktok.fragment;

import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.bytedance.tiktok.R;
import com.bytedance.tiktok.activity.PlayListActivity;
import com.bytedance.tiktok.adapter.VideoAdapter;
import com.bytedance.tiktok.base.BaseFragment;
import com.bytedance.tiktok.bean.DataCreate;
import com.bytedance.tiktok.utils.OnVideoControllerListener;
import com.bytedance.tiktok.view.CommentDialog;
import com.bytedance.tiktok.view.ControllerView;
import com.bytedance.tiktok.view.FullScreenVideoView;
import com.bytedance.tiktok.view.ShareDialog;
import com.bytedance.tiktok.view.viewpagerlayoutmanager.OnViewPagerListener;
import com.bytedance.tiktok.view.viewpagerlayoutmanager.ViewPagerLayoutManager;
import com.bytedance.tiktok.viewmodels.MainActivityViewModel;
import com.bytedance.tiktok.viewmodels.MainFragmentViewModel;

import butterknife.BindView;

import static com.bytedance.tiktok.activity.MainActivity.USER_HOME_PAGE;

/**
 * create on 2020-05-19
 * description 推荐播放页
 */
public class RecommendFragment extends BaseFragment {
    @BindView(R.id.recyclerview)
    RecyclerView recyclerView;
    private VideoAdapter adapter;
    private ViewPagerLayoutManager viewPagerLayoutManager;
    /** 当前播放视频位置 */
    private int curPlayPos = -1;
    private FullScreenVideoView videoView;
    @BindView(R.id.refreshlayout)
    SwipeRefreshLayout refreshLayout;
    private ImageView ivCurCover;
    private int oldPosition;
    private MainActivityViewModel mainViewModel;
    private MainFragmentViewModel mainFragmentViewModel;

    private static final int  LOCATION_PAGE = 0;
    private static final int RECOMMEND_PAGE = 1;

    @Override
    protected int setLayoutId() {
        return R.layout.fragment_recommend;
    }

    @Override
    protected void init() {
        mainViewModel = ViewModelProviders.of(getActivity()).get(MainActivityViewModel.class);
        mainFragmentViewModel = ViewModelProviders.of((FragmentActivity)getContext()).get(MainFragmentViewModel.class);
        adapter = new VideoAdapter(getContext(), DataCreate.datas);
        recyclerView.setAdapter(adapter);
        recyclerView.setItemViewCacheSize(3);

        videoView = new FullScreenVideoView(getContext());

        setViewPagerLayoutManager();

        setRefreshEvent();

        mainViewModel.getState().observe(this, aBoolean -> {
            if (aBoolean) {
                videoView.start();
            } else {
                videoView.pause();
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        videoView.start();
    }

    @Override
    public void onPause() {
        super.onPause();
        videoView.pause();
    }

    @Override
    public void onStop() {
        super.onStop();
        videoView.stopPlayback();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (videoView != null) {
            videoView = null;
        }
    }

    private void setViewPagerLayoutManager() {
        viewPagerLayoutManager = new ViewPagerLayoutManager(getContext());
        recyclerView.setLayoutManager(viewPagerLayoutManager);
        recyclerView.scrollToPosition(PlayListActivity.initPos);

        viewPagerLayoutManager.setOnViewPagerListener(new OnViewPagerListener() {
            @Override
            public void onInitComplete() {
                playCurVideo(PlayListActivity.initPos);
            }

            @Override
            public void onPageRelease(boolean isNext, int position) {
                if (position != oldPosition && ivCurCover != null) {
                    ivCurCover.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onPageSelected(int position, boolean isBottom) {
                oldPosition = position;
                playCurVideo(position);
            }
        });
    }

    private void setRefreshEvent() {
        refreshLayout.setColorSchemeResources(R.color.color_link);
        refreshLayout.setOnRefreshListener(() -> new CountDownTimer(1000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {

            }

            @Override
            public void onFinish() {
                refreshLayout.setRefreshing(false);
            }
        }.start());
    }

    private void playCurVideo(int position) {
        Log.d("message","position:" + position);
        Log.d("message","curPlayPos:" + curPlayPos);
        Log.d("message","oldPosition:" + oldPosition);
        if (position == curPlayPos) {
            return;
        }

        View itemView = viewPagerLayoutManager.findViewByPosition(position);
        if (itemView == null) {
            return;
        }

        ViewGroup rootView = itemView.findViewById(R.id.rl_container);
        ControllerView controllerView = rootView.findViewById(R.id.controller);
        ImageView ivPause = rootView.findViewById(R.id.iv_play);
        ImageView ivCover = rootView.findViewById(R.id.iv_cover);
        ivPause.setAlpha(0.4f);

        //播放暂停事件
        adapter.setOnPlayPauseListener(() -> {
            if (videoView.isPlaying()) {
                videoView.pause();
                ivPause.setVisibility(View.VISIBLE);
            } else {
                videoView.start();
                ivPause.setVisibility(View.GONE);
            }
        });

        //评论点赞事件
        likeShareEvent(controllerView);

        //切换播放视频的作者主页数据
        mainViewModel.getCurUserEvent().postValue(new CurUserBean(DataCreate.datas.get(position).getUserBean()));

        curPlayPos = position;

        //切换播放器位置
        dettachParentView(rootView);

        autoPlayVideo(curPlayPos, ivCover);
    }

    /**
     * 移除videoview父view
     */
    private void dettachParentView(ViewGroup rootView) {
        //1.添加videoview到当前需要播放的item中,添加进item之前，保证ijkVideoView没有父view
        ViewGroup parent = (ViewGroup) videoView.getParent();
        if (parent != null) {
            parent.removeView(videoView);
        }
        rootView.addView(videoView, 0);
    }

    /**
     * 自动播放视频
     */
    private void autoPlayVideo(int position, ImageView ivCover) {
        String bgVideoPath = "android.resource://" + getActivity().getPackageName() + "/" + DataCreate.datas.get(position).getVideoRes();
        videoView.setVideoPath(bgVideoPath);
        videoView.start();
        videoView.setOnPreparedListener(mp -> {
            mp.setLooping(true);
            //延迟取消封面，避免加载视频黑屏
            ivCurCover = ivCover;
            ivCurCover.setVisibility(View.GONE);
        });
    }

    /**
     * 用户操作事件
     */
    private void likeShareEvent(ControllerView controllerView) {
        controllerView.setListener(new OnVideoControllerListener() {
            @Override
            public void onHeadClick() {

                mainViewModel.getPageChangeEvent().postValue(USER_HOME_PAGE);
            }

            @Override
            public void onLikeClick() {

            }

            @Override
            public void onCommentClick() {
                CommentDialog commentDialog = new CommentDialog();
                commentDialog.show(getChildFragmentManager(), "");
            }

            @Override
            public void onShareClick() {
                new ShareDialog().show(getChildFragmentManager(), "");
            }
        });
    }

}
