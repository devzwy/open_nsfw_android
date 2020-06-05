package com.example.open_nsfw_android.view

import android.annotation.SuppressLint
import android.graphics.BitmapFactory
import android.graphics.ImageFormat
import android.graphics.Rect
import android.graphics.YuvImage
import android.hardware.Camera
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.SurfaceHolder
import android.view.Window
import android.view.WindowManager
import android.widget.Toast
import com.example.open_nsfw_android.R
import getNsfwScore
import kotlinx.android.synthetic.main.cameraaty.*
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.util.*

class CameraActivity : AppCompatActivity(), SurfaceHolder.Callback, Camera.PreviewCallback {

    lateinit var mSurfaceHolder: SurfaceHolder
    lateinit var mCamera: Camera
    private var cameraId = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
        setContentView(R.layout.cameraaty)

        mSurfaceHolder = surfaceView.holder
        mSurfaceHolder.addCallback(this)
        iv_back.setOnClickListener {
            try {
                mCamera.setPreviewCallback(null)
                mCamera.stopPreview()
                mCamera.release()
            } catch (e: Exception) {
            }
            finish()
        }
    }

    override fun onBackPressed() {
        try {
            mCamera.setPreviewCallback(null)
            mCamera.stopPreview()
            mCamera.release()
        } catch (e: Exception) {
        }
        super.onBackPressed()
    }

    //翻转摄像机
    fun cameraSwitch() {
        cameraId = if (cameraId == 1) 0 else 1
        mCamera.setPreviewCallback(null)
        mCamera.stopPreview()
        mCamera.release()
        CameraOpen()
    }


    //打开照相机
    fun CameraOpen() {
        try {
            //打开摄像机
            mCamera = Camera.open(cameraId)
            mCamera.setDisplayOrientation(90)
            //绑定Surface并开启预览
            mCamera.setPreviewDisplay(mSurfaceHolder)
            val parameters = mCamera.parameters
//            val focusModes = parameters.supportedFocusModes
//            val size = calculatePerfectSize(
//                parameters.supportedPreviewSizes,
//                1024, 1024
//            )
//            surfaceView.layoutParams.width = size!!.width
//            surfaceView.layoutParams.height = size.height
//
//            parameters.setPreviewSize(size.width, size.height)
            parameters.focusMode = "fixed"
            mCamera.parameters = parameters
            mCamera.startPreview()
            mCamera.setPreviewCallback(this)
            iv_change.setOnClickListener { cameraSwitch() }
        } catch (e: IOException) {
            mCamera.release()
            Toast.makeText(this, "视图创建失败", Toast.LENGTH_SHORT).show()
        }
    }

    override fun surfaceChanged(holder: SurfaceHolder?, format: Int, width: Int, height: Int) {
        val parameters = mCamera.parameters
        mCamera.parameters = parameters
        mCamera.startPreview()
    }

    /**
     * 计算最完美的Size
     * @param sizes
     * @param expectWidth
     * @param expectHeight
     * @return
     */
    fun calculatePerfectSize(
        sizes: List<Camera.Size>, expectWidth: Int,
        expectHeight: Int
    ): Camera.Size? {
        sortList(sizes) // 根据宽度进行排序
        var result = sizes[0]
        var widthOrHeight = false // 判断存在宽或高相等的Size
        // 辗转计算宽高最接近的值
        for (size in sizes) {
            // 如果宽高相等，则直接返回
            if (size.width == expectWidth && size.height == expectHeight) {
                result = size
                break
            }
            // 仅仅是宽度相等，计算高度最接近的size
            if (size.width == expectWidth) {
                widthOrHeight = true
                if (Math.abs(result.height - expectHeight)
                    > Math.abs(size.height - expectHeight)
                ) {
                    result = size
                }
            } else if (size.height == expectHeight) {
                widthOrHeight = true
                if (Math.abs(result.width - expectWidth)
                    > Math.abs(size.width - expectWidth)
                ) {
                    result = size
                }
            } else if (!widthOrHeight) {
                if (Math.abs(result.width - expectWidth)
                    > Math.abs(size.width - expectWidth)
                    && Math.abs(result.height - expectHeight)
                    > Math.abs(size.height - expectHeight)
                ) {
                    result = size
                }
            }
        }
        return result
    }

    /**
     * 排序
     * @param list
     */
    private fun sortList(list: List<Camera.Size>) {
        Collections.sort(list, object : Comparator<Camera.Size?> {

            override fun compare(pre: Camera.Size?, after: Camera.Size?): Int {
                if (pre!!.width > after!!.width) {
                    return 1
                } else if (pre.width < after.width) {
                    return -1
                }
                return 0
            }
        })
    }


    override fun surfaceDestroyed(holder: SurfaceHolder?) {
        mCamera.stopPreview()
        mCamera.release()
    }


    override fun surfaceCreated(holder: SurfaceHolder?) {
        CameraOpen()
    }

    @SuppressLint("SetTextI18n")
    override fun onPreviewFrame(data: ByteArray?, camera: Camera?) {
        mCamera.addCallbackBuffer(data)

        val size = mCamera.parameters.previewSize;
        val image = YuvImage(data, ImageFormat.NV21, size.width, size.height, null);
        val stream = ByteArrayOutputStream()
        image.compressToJpeg(Rect(0, 0, size.width, size.height), 80, stream)
        val bmp = BitmapFactory.decodeByteArray(stream.toByteArray(), 0, stream.size())
        bmp.getNsfwScore()
        val spText = bmp.getNsfwScore().toString().split(",")
        textView.text = spText[0] + "\n" + spText[1]
        stream.close()
    }

}