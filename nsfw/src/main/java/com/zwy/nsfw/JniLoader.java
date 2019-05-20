package com.zwy.nsfw;

public class JniLoader {

    static  {
        System.loadLibrary("native-lib");
    }

    public static native void argb2bgr(byte[] rgbSrc,byte[] bgrDesc);
}
