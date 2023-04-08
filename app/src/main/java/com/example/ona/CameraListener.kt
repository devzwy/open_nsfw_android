package com.example.ona

import android.hardware.Camera

interface CameraListener {

    /**
     * 摄像头打开的回调
     */
    fun onCameraOpen(mCamera: Camera, mCameraId: Int, previewSize: Camera.Size)

    /**
     * 摄像头关闭回调
     */
    fun onCameraClose()

    /**
     * 异常回调
     */
    fun onCameraError(e: Exception)

    /**
     * 开始预览回调
     */
    fun onPreview(nv21: ByteArray, camera: Camera)

}