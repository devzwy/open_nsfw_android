#include <jni.h>
#include <string>

extern "C"
JNIEXPORT void JNICALL
Java_com_zwy_nsfw_JniLoader_argb2bgr(JNIEnv *env, jobject thiz,
                                                                   jbyteArray rgbSrc_,
                                                                   jbyteArray bgrDesc_) {
    jbyte *rgbSrc = env->GetByteArrayElements(rgbSrc_, NULL);
    jbyte *bgrDesc = env->GetByteArrayElements(bgrDesc_, NULL);
    printf("s[]=%s\n","C+++++++++");/*输出数组字符串s[]=Hello,Comrade*/
    int wh = env->GetArrayLength(rgbSrc_) / 4 ;


//#pragma omp parallel for
    for (int i = 0; i < wh; ++i) {
        bgrDesc[i * 3] = rgbSrc[i * 4 + 2];		//B
        bgrDesc[i * 3 + 1] = rgbSrc[i * 4 + 1]; 	//G
        bgrDesc[i * 3 + 2] = rgbSrc[i * 4 ];		//R
    }

    env->ReleaseByteArrayElements(rgbSrc_, rgbSrc, JNI_ABORT);
    env->ReleaseByteArrayElements(bgrDesc_, bgrDesc, JNI_COMMIT);
}
