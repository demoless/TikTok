package com.bytedance.tiktok.decode

import android.media.MediaFormat
import java.nio.ByteBuffer

class VideoExtractor(filePath: String): IExtractor {
    private val mExtractor by lazy {
        MMExtractor(filePath)
    }
    override fun getFormat(): MediaFormat? {
        return mExtractor.getVideoFormat()
    }

    override fun readBuffer(byteBuffer: ByteBuffer): Int {
        return mExtractor.readBuffer(byteBuffer)
    }

    override fun getCurrentTimestamp(): Long {
        return mExtractor.getCurrentTimestamp()
    }

    override fun seek(pos: Long): Long {
        return mExtractor.seek(pos)
    }

    override fun setStartPos(pos: Long) {
        mExtractor.setStartPos(pos)
    }

    override fun stop() {
        mExtractor.stop()
    }
}