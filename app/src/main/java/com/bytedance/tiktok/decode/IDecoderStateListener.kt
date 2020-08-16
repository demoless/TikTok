package com.bytedance.tiktok.decode

interface IDecoderStateListener {

    fun decoderPrepare(iDecoder: IDecoder)
    fun decoderFinish(iDecoder: IDecoder)
    fun decoderPause(iDecoder: IDecoder)
    fun decoderRunning(iDecoder: IDecoder)
    fun decoderError(iDecoder: IDecoder, s: String)
    fun decoderDestroy(iDecoder: IDecoder)

}
