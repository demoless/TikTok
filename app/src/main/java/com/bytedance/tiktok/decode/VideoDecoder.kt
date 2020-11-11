package com.bytedance.tiktok.decode

import android.content.Context
import android.media.MediaCodec
import android.media.MediaFormat
import android.util.Log
import android.view.Surface
import android.view.SurfaceHolder
import android.view.SurfaceView
import java.nio.ByteBuffer

class VideoDecoder(private val context: Context,filePath: String): BaseDecoder(filePath) {

    companion object{
        private const val TAG = "VideoDecoder"
    }

    private val surfaceView: SurfaceView by lazy {
        SurfaceView(context)
    }

    private var surface: Surface? = null
    override fun check(): Boolean {
        if (surface == null) {
            mStateListener?.decoderError(this,"surface is null")
            return false
        }
        return true
    }

    override fun initExtractor(path: String): IExtractor {
        return VideoExtractor(context,path)
    }

    override fun initSpecParams(format: MediaFormat) {

    }

    override fun initRender(): Boolean {
        return true
    }

    override fun configCodec(codec: MediaCodec, format: MediaFormat): Boolean {
        if (surface != null) {
            codec.configure(format, surface , null, 0)
            notifyDecode()
        } else {
            surfaceView.holder.addCallback(object : SurfaceHolder.Callback2 {
                override fun surfaceRedrawNeeded(holder: SurfaceHolder) {
                }

                override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {
                    Log.d(TAG,"surfaceChanged")
                }

                override fun surfaceDestroyed(holder: SurfaceHolder) {
                    Log.d(TAG,"surfaceDestroyed")
                }

                override fun surfaceCreated(holder: SurfaceHolder) {
                    surface = holder.surface
                    configCodec(codec, format)
                }
            })

            return false
        }
        return true
    }

    override fun doneDecode() {

    }

    override fun render(outputBuffers: ByteBuffer, bufferInfo: MediaCodec.BufferInfo) {

    }

    override fun reStart() {

    }
}