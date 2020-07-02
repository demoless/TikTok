package com.bytedance.tiktok.bean;

/**
 * create on 2020-06-04
 * description
 */
public class CommentBean {
    private String content;
    private VideoBean.UserBean userBean;
    private int likeCount;
    private boolean isLiked;

    public String getContent() {
        return content == null ? "" : content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public VideoBean.UserBean getUserBean() {
        return userBean;
    }

    public void setUserBean(VideoBean.UserBean userBean) {
        this.userBean = userBean;
    }

    public int getLikeCount() {
        return likeCount;
    }

    public void setLikeCount(int likeCount) {
        this.likeCount = likeCount;
    }

    public boolean isLiked() {
        return isLiked;
    }

    public void setLiked(boolean liked) {
        isLiked = liked;
    }
}
