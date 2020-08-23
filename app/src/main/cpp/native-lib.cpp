#include <jni.h>
#include <string>
#include <unistd.h>

extern "C" {
    #include "ffmpeg/include/libavutil/avutil.h"
    #include "ffmpeg/include/libavcodec/codec.h"
    #include "ffmpeg/include/libavcodec/avcodec.h"
    JNIEXPORT jstring JNICALL
    Java_com_bytedance_tiktok_test_VideoDecoderActivity_ffmpegInfo(JNIEnv *env, jobject  /* this */) {

        char info[40000] = {0};
        AVCodec *c_temp = av_codec_next(NULL);
        while (c_temp != NULL) {
            if (c_temp->decode != NULL) {
                sprintf(info, "%sdecode:", info);
                switch (c_temp->type) {
                    case AVMEDIA_TYPE_VIDEO:
                        sprintf(info, "%s(video):", info);
                        break;
                    case AVMEDIA_TYPE_AUDIO:
                        sprintf(info, "%s(audio):", info);
                        break;
                    default:
                        sprintf(info, "%s(other):", info);
                        break;
            }
                sprintf(info, "%s[%10s]\n", info, c_temp->name);
            } else {
                sprintf(info, "%sencode:", info);
        }
            c_temp = c_temp->next;
        }
        return env->NewStringUTF(info);
    }
}
