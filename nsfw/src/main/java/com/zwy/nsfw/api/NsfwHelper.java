package com.zwy.nsfw.api;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.Bitmap;
import android.util.Log;
import com.zwy.nsfw.Classifier;

import java.io.IOException;

public class NsfwHelper {
    @SuppressLint("StaticFieldLeak")
    private static NsfwHelper nsfwHelper;
    private Activity activity;
    private Classifier classifier;

    public static NsfwHelper getInstance(Activity activity, Boolean isAddGpuDelegate, int numThreads) {
        synchronized (NsfwHelper.class) {
            if (nsfwHelper == null) {
                nsfwHelper = new NsfwHelper(activity, isAddGpuDelegate, numThreads);
            }
        }
        return nsfwHelper;
    }

    private NsfwHelper(Activity activity, Boolean isAddGpuDelegate, int numThreads) {
        try {
            this.activity = activity;
            classifier = Classifier.create(activity, isAddGpuDelegate, numThreads);
        } catch (IOException e) {
            e.printStackTrace();
            Log.e(Classifier.TAG, "Tensorflow Lite Image Classifier Initialization Error,e:" + e);
        }
    }


    /**
     * 同步扫描
     *
     * @param bitmap
     * @return nsfw/sfw
     */
    public NsfwBean scanBitmapSyn(Bitmap bitmap) {
        synchronized (NsfwHelper.class) {
            return classifier.run(bitmap);
        }
    }


    /**
     * 异步扫描
     *
     * @param bitmap
     * @param onScanBitmapListener void onSuccess(float sfw, float nsfw);
     */
    public void scanBitmap(final Bitmap bitmap, final OnScanBitmapListener onScanBitmapListener) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                final NsfwBean nsfwBean = scanBitmapSyn(bitmap);
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        onScanBitmapListener.onSuccess(nsfwBean.getSfw(), nsfwBean.getNsfw());
                    }
                });
            }
        }).start();
    }

    public void destroy() {
        classifier.close();
        activity = null;
        nsfwHelper = null;
    }

    public static interface OnScanBitmapListener {
        void onSuccess(float sfw, float nsfw);
    }


}
