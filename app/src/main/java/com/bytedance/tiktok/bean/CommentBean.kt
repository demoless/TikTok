package com.bytedance.tiktok.bean

data class CommentBean(
        var content: String,
        var userBean: VideoBean.UserBean,
        var likeCount: Int,
        var isLiked:Boolean = false )