package com.bytedance.tiktok.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import butterknife.BindView
import butterknife.ButterKnife
import com.bytedance.tiktok.R
import com.bytedance.tiktok.activity.PlayListActivity
import com.bytedance.tiktok.base.BaseRvAdapter
import com.bytedance.tiktok.base.BaseRvViewHolder
import com.bytedance.tiktok.bean.VideoBean
import com.bytedance.tiktok.view.IconFontTextView

/**
 * created by demoless on 2020/11/5
 * description:
 */
class GridVideoAdapterKt(context: Context, datas: List<VideoBean>)
    : BaseRvAdapter<VideoBean, GridVideoAdapterKt.GridVideoViewHolder>(context, datas) {

    class GridVideoViewHolder(itemView: View) : BaseRvViewHolder(itemView) {
        init {
            ButterKnife.bind(itemView)
        }
        @BindView(R.id.iv_cover)
        var ivCover: ImageView? = null

        @BindView(R.id.tv_content)
        var tvContent: TextView? = null

        @BindView(R.id.tv_distance)
        var tvDistance: IconFontTextView? = null

        @BindView(R.id.iv_head)
        var ivHead: ImageView? = null
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GridVideoViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_gridvideo, parent, false)
        return GridVideoViewHolder(view)
    }

    override fun onBindData(holder: GridVideoViewHolder, data: VideoBean, position: Int) {
        holder.ivCover?.setBackgroundResource(data.coverRes)
        holder.tvContent?.text = data.content
        holder.tvDistance?.text = data.distance.toString() + " km"
        holder.ivHead?.setImageResource(data.userBean.head)
        holder.itemView.setOnClickListener { _: View? ->
            PlayListActivity.initPos = position
            context.startActivity(Intent(context, PlayListActivity::class.java))
        }
    }
}