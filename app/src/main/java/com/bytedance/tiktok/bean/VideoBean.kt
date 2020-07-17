package com.bytedance.tiktok.bean

import androidx.annotation.DrawableRes


data class VideoBean @JvmOverloads constructor(
         var  videoRes:Int = 0,
        @DrawableRes var coverRes:Int = 0,
         var content:String = "",
         var userBean:UserBean ,
         var isLiked:Boolean = false,
         var distance:Float = 0f,
         var isFocused:Boolean = false,
         var likeCount:Int = 0,
         var commentCount:Int= 0,
         var shareCount:Int = 0
        ) {
    data class UserBean @JvmOverloads constructor(var uid :Int = 0,var nickName: String = "",
                                                  var head: Int = 0,
                                    var sign: String = "" ,
                                     var isFocused: Boolean = false,
                                     var subCount: Int = 0, var focusCount: Int = 0,
                                                  var fansCount: Int = 0,
                                                  var workCount: Int = 0,
                                                  var dynamicCount: Int = 0,
                                                  var likeCount:Int = 0)
}