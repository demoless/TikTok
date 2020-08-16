package com.bytedance.tiktok.decode

import android.media.MediaCodec
import android.media.MediaFormat
import java.nio.ByteBuffer

class VideoDecoder(filePath: String) : BaseDecoder(filePath) {
    override fun doneDecode() {
        TODO("Not yet implemented")
    }

    override fun render(outputBuffers: ByteBuffer, bufferInfo: MediaCodec.BufferInfo) {
        TODO("Not yet implemented")
    }

    override fun check(): Boolean {
        TODO("Not yet implemented")
    }

    override fun initExtractor(path: String): IExtractor {
        TODO("Not yet implemented")
    }

    override fun initSpecParams(format: MediaFormat) {
        TODO("Not yet implemented")
    }

    override fun initRender(): Boolean {
        TODO("Not yet implemented")
    }

    override fun configCodec(codec: MediaCodec, format: MediaFormat): Boolean {
        TODO("Not yet implemented")
    }

    override fun reStart() {
        TODO("Not yet implemented")
    }
}