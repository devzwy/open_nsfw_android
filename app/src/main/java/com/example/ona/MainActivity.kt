package com.example.ona

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.ImageFormat
import android.graphics.Point
import android.graphics.Rect
import android.graphics.YuvImage
import android.hardware.Camera
import android.os.Bundle
import android.view.TextureView
import android.view.ViewTreeObserver
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import io.github.nsfw.decodeNsfwScore
import java.io.ByteArrayOutputStream
import kotlin.system.exitProcess


class MainActivity : AppCompatActivity(), ViewTreeObserver.OnGlobalLayoutListener, CameraListener {

    val cameraView: TextureView by lazy {
        findViewById(R.id.cameraView)
    }

    val tvl: TextView by lazy {
        findViewById(R.id.tvl)
    }

    val tvr: TextView by lazy {
        findViewById(R.id.tvr)
    }

    var mDualCameraHelper:DualCameraHelper?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        cameraView.viewTreeObserver.addOnGlobalLayoutListener(this)

    }


    private val REQUEST_EXTERNAL_STORAGE_PERMISSION = 1

    private fun requestExternalStoragePermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
            != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
            != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.CAMERA
                ),
                REQUEST_EXTERNAL_STORAGE_PERMISSION
            )
        } else {
            // Permission already granted, you can perform your operation here
            ActivityCompat.requestPermissions(
                this,
                arrayOf(
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.CAMERA
                ),
                REQUEST_EXTERNAL_STORAGE_PERMISSION
            )
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String?>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            REQUEST_EXTERNAL_STORAGE_PERMISSION -> {
                if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, "READ_EXTERNAL_STORAGE_SUCC", Toast.LENGTH_SHORT).show()

                    mDualCameraHelper = DualCameraHelper.Builder()
                        .cameraListener(this)
                        .specificCameraId(0)
                        .previewOn(cameraView)
                        .previewViewSize(Point(cameraView.measuredWidth, cameraView.measuredHeight)).build()
                    mDualCameraHelper?.init()
                    mDualCameraHelper?.start()
                } else {
                    Toast.makeText(this, "READ_EXTERNAL_STORAGE_ERROR,exit app~", Toast.LENGTH_SHORT).show()
                    exitProcess(0)
                }
            }
        }
    }

    override fun onGlobalLayout() {
        cameraView.viewTreeObserver.removeOnGlobalLayoutListener(this)
        requestExternalStoragePermission()
    }

    override fun onCameraOpen(mCamera: Camera, mCameraId: Int, previewSize: Camera.Size) {

    }

    override fun onCameraClose() {

    }

    override fun onCameraError(e: Exception) {

    }

    var mFrameCount = 0

    override fun onPreview(nv21Data: ByteArray, camera: Camera) {

        mFrameCount++

        if (mFrameCount % 10 == 0){
            val width = mDualCameraHelper?.previewSize?.width?:0
            val height = mDualCameraHelper?.previewSize?.height?:0

            val yuvImage = YuvImage(nv21Data, ImageFormat.NV21, width, height, null)
            val out = ByteArrayOutputStream()
            yuvImage.compressToJpeg(Rect(0, 0, width, height), 100, out)
            val imageBytes: ByteArray = out.toByteArray()
            val a = imageBytes.decodeNsfwScore()
            runOnUiThread {
                tvl.text = "${a[0]}"
                tvr.text = "${a[1]}"

            }
        }
    }


}