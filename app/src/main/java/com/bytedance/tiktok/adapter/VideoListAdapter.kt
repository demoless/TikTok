package com.bytedance.tiktok.adapter

import android.media.MediaPlayer
import android.util.Log
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.adapter.FragmentViewHolder
import com.bytedance.tiktok.bean.DataCreate
import com.bytedance.tiktok.fragment.IVideoController
import com.bytedance.tiktok.fragment.VideoItemFragment
import java.util.*

/**
 * created by demoless on 2020/7/5
 * description:
 */
class VideoListAdapter constructor(fragment: Fragment)
    : FragmentStateAdapter(fragment) {

    private var mediaPlayer:MediaPlayer? = null
    private var videoController: IVideoController? = null
    private val fragments = LinkedList<VideoItemFragment>()

    fun pauseVideo() {
        videoController?.let {
            it.pauseVideo()
            Log.e("VideoListAdapter","VideoListAdapter:pauseVideo")
        }
    }

    override fun getItemCount(): Int {
        return DataCreate.datas.size
    }

    @ExperimentalStdlibApi
    override fun createFragment(position: Int): VideoItemFragment {
        return VideoItemFragment(DataCreate.datas[position],position).apply {
            videoController = this
        }
    }

    private fun obtainFragment(position: Int): VideoItemFragment {
        if (fragments.isEmpty()) {
            fragments.addFirst(VideoItemFragment(DataCreate.datas[position],position))
        }
        if (fragments.size == 3 && position == fragments.last.position) {
            fragments.removeFirst()
        }
        if (fragments.size == 3 && position == fragments.first.position) {
            fragments.removeLast()
        }
        if (fragments.size < 3 && itemCount > position+1) {
            fragments.addLast(VideoItemFragment(DataCreate.datas[position+1],position+1))
        }
        if (fragments.size < 3 && position-1 >= 0) {
            fragments.addFirst(VideoItemFragment(DataCreate.datas[position-1],position-1))
        }
        var result = fragments[0]
        if (position != 0) {
            result = fragments[1]
        }
        videoController = result
        return result
    }

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        Log.e("message","VideoListAdapter:onAttachedToRecyclerView")
    }

    override fun onViewDetachedFromWindow(holder: FragmentViewHolder) {
        super.onViewDetachedFromWindow(holder)
        Log.e("message","VideoListAdapter:onViewDetachedFromWindow")
    }
}