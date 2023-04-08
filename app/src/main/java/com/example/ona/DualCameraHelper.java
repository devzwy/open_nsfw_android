package com.example.ona;

import android.graphics.ImageFormat;
import android.graphics.Point;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.util.Log;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.TextureView;
import android.view.View;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;


/**
 * <p>
 * 由于IR摄像头和RGB摄像头的默认分辨率可能不同，为了让两者相同，该类做了以下操作：
 * 1. 获取两者支持的分辨率列表到到静态变量{@link DualCameraHelper#rgbSupportedPreviewSizes}及{@link DualCameraHelper#irSupportedPreviewSizes}中，
 * 2. 使用{@link DualCameraHelper#getCommonSupportedPreviewSize()}方法取分辨率的交集，
 * 3. 使用{@link DualCameraHelper#getBestSupportedSize(List, Point)}取最佳分辨率使两个摄像头分辨率尽可能相同
 */
public class DualCameraHelper implements Camera.PreviewCallback {
    private static List<Camera.Size> rgbSupportedPreviewSizes;
    private static List<Camera.Size> irSupportedPreviewSizes;
    private static final String TAG = "CameraHelper";
    private Camera mCamera;
    private int mCameraId;
    private Point previewViewSize;
    private View previewDisplayView;
    private Camera.Size previewSize;
    private Point specificPreviewSize;
    private int displayOrientation = 0;
    private int rotation;
    private int additionalRotation;
    private boolean isMirror = false;

    private Integer specificCameraId = null;
    private CameraListener cameraListener;
    private static final int MIN_PREVIEW_WIDTH = 720;
    private static final int MIN_PREVIEW_HEIGHT = 720;

    private DualCameraHelper(Builder builder) {
        previewDisplayView = builder.previewDisplayView;
        specificCameraId = builder.specificCameraId;
        cameraListener = builder.cameraListener;
        rotation = builder.rotation;
        additionalRotation = builder.additionalRotation;
        previewViewSize = builder.previewViewSize;
        specificPreviewSize = builder.previewSize;
        if (builder.previewDisplayView instanceof TextureView) {
            isMirror = builder.isMirror;
        } else if (isMirror) {
            throw new RuntimeException("mirror is effective only when the preview is on a textureView");
        }
    }

    public Camera.Size getPreviewSize() {
        return previewSize;
    }

    public void init() {
        if (previewDisplayView instanceof TextureView) {
            ((TextureView) this.previewDisplayView).setSurfaceTextureListener(textureListener);
        } else if (previewDisplayView instanceof SurfaceView) {
            ((SurfaceView) previewDisplayView).getHolder().addCallback(surfaceCallback);
        }

        if (isMirror) {
            previewDisplayView.setScaleX(-1);
        }
    }

    public List<Camera.Size> getCommonSupportedPreviewSize() {
        /**
         * irSupportedPreviewSizes 和 rgbSupportedPreviewSizes 为null才去获取，
         * 不为null就没必要获取了，而且此时有可能该camera已处于打开状态，无法打开camera
         */
        if (rgbSupportedPreviewSizes == null) {
            Camera rgbCamera = Camera.open(Camera.CameraInfo.CAMERA_FACING_BACK);
            rgbSupportedPreviewSizes = rgbCamera.getParameters().getSupportedPreviewSizes();
            rgbCamera.release();
        }
        try {
            if (irSupportedPreviewSizes == null) {
                Camera irCamera = Camera.open(Camera.CameraInfo.CAMERA_FACING_FRONT);
                irSupportedPreviewSizes = irCamera.getParameters().getSupportedPreviewSizes();
                irCamera.release();
            }
        } catch (RuntimeException e) {
            e.printStackTrace();
            irSupportedPreviewSizes = rgbSupportedPreviewSizes;
        }
        List<Camera.Size> commonPreviewSizes = new ArrayList<>();
        for (Camera.Size rgbPreviewSize : rgbSupportedPreviewSizes) {
            if (rgbPreviewSize.width < MIN_PREVIEW_WIDTH || rgbPreviewSize.height < MIN_PREVIEW_HEIGHT) {
                continue;
            }
            for (Camera.Size irPreviewSize : irSupportedPreviewSizes) {
                if (irPreviewSize.width == rgbPreviewSize.width && irPreviewSize.height == rgbPreviewSize.height) {
                    commonPreviewSizes.add(rgbPreviewSize);
                }
            }
        }
        return commonPreviewSizes;
    }

    /**
     * 回传当前使用的cameraID，若当前没打开相机，回传-1
     *
     * @return cameraId，失败回传-1
     */
    public int getCurrentOpenedCameraId() {
        if (mCamera == null) {
            return -1;
        }
        return mCameraId;
    }

    public void start() {
        synchronized (this) {
            if (mCamera != null) {
                return;
            }
            List<Camera.Size> supportedPreviewSize = getCommonSupportedPreviewSize();
            //相机数量为2则打开1,1则打开0,相机ID 1为前置，0为后置
            mCameraId = Camera.getNumberOfCameras() - 1;
            //若指定了相机ID且该相机存在，则打开指定的相机
            if (specificCameraId != null && specificCameraId <= mCameraId) {
                mCameraId = specificCameraId;
            }

            //没有相机
            if (mCameraId == -1) {
                if (cameraListener != null) {
                    cameraListener.onCameraError(new Exception("camera not found"));
                }
                return;
            }
            if (mCamera == null) {
                mCamera = Camera.open(mCameraId);
            }
            displayOrientation = getCameraOri(rotation);
            mCamera.setDisplayOrientation(displayOrientation);
            try {
                Camera.Parameters parameters = mCamera.getParameters();
                parameters.setPreviewFormat(ImageFormat.NV21);

                //预览大小设置
                previewSize = parameters.getPreviewSize();
                if (supportedPreviewSize != null && supportedPreviewSize.size() > 0) {
                    previewSize = getBestSupportedSize(supportedPreviewSize, previewViewSize);
                }
                Log.i(TAG, "start: " + previewSize.width + "x" + previewSize.height);
                parameters.setPreviewSize(previewSize.width, previewSize.height);

                //对焦模式设置
                List<String> supportedFocusModes = parameters.getSupportedFocusModes();
                if (supportedFocusModes != null && supportedFocusModes.size() > 0) {
                    if (supportedFocusModes.contains(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE)) {
                        parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);
                    } else if (supportedFocusModes.contains(Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO)) {
                        parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO);
                    } else if (supportedFocusModes.contains(Camera.Parameters.FOCUS_MODE_AUTO)) {
                        parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_AUTO);
                    }
                }
                mCamera.setParameters(parameters);
                if (previewDisplayView instanceof TextureView) {
                    mCamera.setPreviewTexture(((TextureView) previewDisplayView).getSurfaceTexture());
                } else {
                    mCamera.setPreviewDisplay(((SurfaceView) previewDisplayView).getHolder());
                }
                mCamera.setPreviewCallback(this);
                Thread.sleep(1000);
                mCamera.startPreview();

                if (cameraListener != null) {
                    cameraListener.onCameraOpen(mCamera, mCameraId, previewSize);
                }
            } catch (Exception e) {
                if (cameraListener != null) {
                    cameraListener.onCameraError(e);
                }
            }
        }
    }

    public void switchCameraId() {
        mCameraId = 1 - mCameraId;
        if (specificCameraId != null) {
            specificCameraId = 1 - specificCameraId;
        }
    }

    private int getCameraOri(int rotation) {
        int degrees = rotation * 90;
        switch (rotation) {
            case Surface.ROTATION_0:
                degrees = 0;
                break;
            case Surface.ROTATION_90:
                degrees = 90;
                break;
            case Surface.ROTATION_180:
                degrees = 180;
                break;
            case Surface.ROTATION_270:
                degrees = 270;
                break;
            default:
                break;
        }
        additionalRotation /= 90;
        additionalRotation *= 90;
        degrees += additionalRotation;
        int result;
        Camera.CameraInfo info = new Camera.CameraInfo();
        Camera.getCameraInfo(mCameraId, info);
        if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
            result = (info.orientation + degrees) % 360;
            result = (360 - result) % 360;
        } else {
            result = (info.orientation - degrees + 360) % 360;
        }
        return result;
    }

    /**
     * 停止预览
     */
    public void stop() {
        synchronized (this) {
            if (mCamera == null) {
                return;
            }
            mCamera.setPreviewCallback(null);
            mCamera.stopPreview();
            mCamera.release();
            mCamera = null;
            if (cameraListener != null) {
                cameraListener.onCameraClose();
            }
        }
    }

    public boolean isStopped() {
        synchronized (this) {
            return mCamera == null;
        }
    }

    /**
     * 释放操作
     */
    public void release() {
        synchronized (this) {
            stop();
            previewDisplayView = null;
            specificCameraId = null;
            cameraListener = null;
            previewViewSize = null;
            specificPreviewSize = null;
            previewSize = null;
        }
    }

    /**
     * 获取候选分辨率列表中最接近预览view大小的分辨率
     *
     * @param sizes           支持的分辨率
     * @param previewViewSize 预览view的大小
     * @return 最接近预览view大小的分辨率
     */
    private Camera.Size getBestSupportedSize(List<Camera.Size> sizes, Point previewViewSize) {
        if (sizes == null || sizes.size() == 0) {
            return mCamera.getParameters().getPreviewSize();
        }
        Camera.Size[] tempSizes = sizes.toArray(new Camera.Size[0]);
        Arrays.sort(tempSizes, new Comparator<Camera.Size>() {
            @Override
            public int compare(Camera.Size o1, Camera.Size o2) {
                if (o1.width > o2.width) {
                    return -1;
                } else if (o1.width == o2.width) {
                    return o1.height > o2.height ? -1 : 1;
                } else {
                    return 1;
                }
            }
        });
        sizes = Arrays.asList(tempSizes);

        Camera.Size bestSize = sizes.get(0);
        float previewViewRatio;
        if (previewViewSize != null) {
            previewViewRatio = (float) previewViewSize.x / (float) previewViewSize.y;
        } else {
            previewViewRatio = (float) bestSize.width / (float) bestSize.height;
        }

        if (previewViewRatio > 1) {
            previewViewRatio = 1 / previewViewRatio;
        }
        boolean isNormalRotate = (additionalRotation % 180 == 0);

        for (Camera.Size s : sizes) {
            if (specificPreviewSize != null && specificPreviewSize.x == s.width && specificPreviewSize.y == s.height) {
                return s;
            }
            if (isNormalRotate) {
                if (Math.abs((s.height / (float) s.width) - previewViewRatio) < Math.abs(bestSize.height / (float) bestSize.width - previewViewRatio)) {
                    bestSize = s;
                }
            } else {
                if (Math.abs((s.width / (float) s.height) - previewViewRatio) < Math.abs(bestSize.width / (float) bestSize.height - previewViewRatio)) {
                    bestSize = s;
                }
            }
        }
        return bestSize;
    }

    public List<Camera.Size> getSupportedPreviewSizes() {
        if (mCamera == null) {
            return null;
        }
        return mCamera.getParameters().getSupportedPreviewSizes();
    }

    public List<Camera.Size> getSupportedPictureSizes() {
        if (mCamera == null) {
            return null;
        }
        return mCamera.getParameters().getSupportedPictureSizes();
    }


    @Override
    public void onPreviewFrame(byte[] nv21, Camera camera) {
        if (cameraListener != null) {
            cameraListener.onPreview(nv21, camera);
        }
    }

    private TextureView.SurfaceTextureListener textureListener = new TextureView.SurfaceTextureListener() {
        @Override
        public void onSurfaceTextureAvailable(SurfaceTexture surfaceTexture, int width, int height) {
//            start();
            if (mCamera != null) {
                try {
                    mCamera.setPreviewTexture(surfaceTexture);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        @Override
        public void onSurfaceTextureSizeChanged(SurfaceTexture surfaceTexture, int width, int height) {
            Log.i(TAG, "onSurfaceTextureSizeChanged: " + width + "  " + height);
        }

        @Override
        public boolean onSurfaceTextureDestroyed(SurfaceTexture surfaceTexture) {
            stop();
            return false;
        }

        @Override
        public void onSurfaceTextureUpdated(SurfaceTexture surfaceTexture) {

        }
    };
    private SurfaceHolder.Callback surfaceCallback = new SurfaceHolder.Callback() {
        @Override
        public void surfaceCreated(SurfaceHolder holder) {
//            start();
            if (mCamera != null) {
                try {
                    mCamera.setPreviewDisplay(holder);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        @Override
        public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

        }

        @Override
        public void surfaceDestroyed(SurfaceHolder holder) {
            stop();
        }
    };

    public void changeDisplayOrientation(int rotation) {
        if (mCamera != null) {
            this.rotation = rotation;
            displayOrientation = getCameraOri(rotation);
            mCamera.setDisplayOrientation(displayOrientation);
//            if (cameraListener != null) {
//                cameraListener.onCameraConfigurationChanged(mCameraId, displayOrientation);
//            }
        }
    }

    public static final class Builder {

        /**
         * 预览显示的view，目前仅支持surfaceView和textureView
         */
        private View previewDisplayView;

        /**
         * 是否镜像显示，只支持textureView
         */
        private boolean isMirror;
        /**
         * 指定的相机ID
         */
        private Integer specificCameraId;
        /**
         * 事件回调
         */
        private CameraListener cameraListener;
        /**
         * 屏幕的长宽，在选择最佳相机比例时用到
         */
        private Point previewViewSize;
        /**
         * 传入getWindowManager().getDefaultDisplay().getRotation()的值即可
         */
        private int rotation;
        /**
         * 指定的预览宽高，若系统支持则会以这个预览宽高进行预览
         */
        private Point previewSize;

        /**
         * 额外的旋转角度（用于适配一些定制设备）
         */
        private int additionalRotation;

        public Builder() {
        }


        public Builder previewOn(View val) {
            if (val instanceof SurfaceView || val instanceof TextureView) {
                previewDisplayView = val;
                return this;
            } else {
                throw new RuntimeException("you must preview on a textureView or a surfaceView");
            }
        }


        public Builder isMirror(boolean val) {
            isMirror = val;
            return this;
        }

        public Builder previewSize(Point val) {
            previewSize = val;
            return this;
        }

        public Builder previewViewSize(Point val) {
            previewViewSize = val;
            return this;
        }

        public Builder rotation(int val) {
            rotation = val;
            return this;
        }

        public Builder additionalRotation(int val) {
            additionalRotation = val;
            return this;
        }

        public Builder specificCameraId(Integer val) {
            specificCameraId = val;
            return this;
        }

        public Builder cameraListener(CameraListener val) {
            cameraListener = val;
            return this;
        }

        public DualCameraHelper build() {
            if (previewViewSize == null) {
                Log.e(TAG, "previewViewSize is null, now use default previewSize");
            }
            if (cameraListener == null) {
                Log.e(TAG, "cameraListener is null, callback will not be called");
            }
            if (previewDisplayView == null) {
                throw new RuntimeException("you must preview on a textureView or a surfaceView");
            }
            return new DualCameraHelper(this);
        }
    }

    /**
     * 根据设置的额外旋转角度旋转
     *
     * @param additionalRotation 额外旋转角度
     * @return 当前显示旋转角度
     */
    public int rotateAdditional(int additionalRotation) {
        this.additionalRotation = additionalRotation;
        int cameraOri = getCameraOri(rotation);
        if (mCamera == null) {
            start();
            return cameraOri;
        }
        mCamera.setDisplayOrientation(cameraOri);
        return cameraOri;
    }

    public void setSpecificPreviewSize(Point specificPreviewSize) {
        this.specificPreviewSize = specificPreviewSize;
    }

    public static boolean hasDualCamera() {
        return Camera.getNumberOfCameras() > 1;
    }

    public static boolean canOpenDualCamera() {
        Camera camera0 = null;
        Camera camera1 = null;
        boolean can = true;
        try {
            camera0 = Camera.open(Camera.CameraInfo.CAMERA_FACING_BACK);
            camera1 = Camera.open(Camera.CameraInfo.CAMERA_FACING_FRONT);
        } catch (Exception e) {
            can = false;
        }
        if (camera0 != null) {
            camera0.release();
        }
        if (camera1 != null) {
            camera1.release();
        }
        return can;
    }

}
