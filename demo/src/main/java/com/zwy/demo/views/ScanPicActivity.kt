package com.zwy.demo.views

import android.Manifest
import android.os.Bundle
import com.zwy.demo.R
import com.zwy.demo.base.BaseActivity
import com.zwy.demo.databinding.ScanPicBindingView
import com.zwy.demo.models.ScanPicViewModel
import com.zwy.demo.utils.ParmKey2ScanPicAty
import com.zwy.demo.utils.selectImgFromD
import pub.devrel.easypermissions.EasyPermissions
import pub.devrel.easypermissions.PermissionRequest

class ScanPicActivity : BaseActivity<ScanPicBindingView, ScanPicViewModel>() {

    val requestCode = 0x01
    private fun _requestPermissions() {
        if (!EasyPermissions.hasPermissions(
                this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            )
        ) {
            EasyPermissions.requestPermissions(
                PermissionRequest.Builder(
                    this,
                    0x01,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                ).build()
            )
            return
        }
    }

    override fun initData() {
        _requestPermissions()
        binding.dataList = viewModel.imgData
        intent.getIntExtra(ParmKey2ScanPicAty, 0x00).let {
            if (it == 1 || it == 3)
                viewModel.startScann(it)
            else selectImgFromD(requestCode)
        }
    }

    /**
     * 初始化根布局
     *
     * @return 布局layout的id
     */
    override fun initContentView(savedInstanceState: Bundle?): Int = R.layout.activity_scan_pic


//    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
//        super.onActivityResult(requestCode, resultCode, data)
//        if (requestCode == 0x01 && resultCode == RESULT_OK) {
//                PictureSelector.obtainMultipleResult(data)?.let {
//                mMainAdapter.setNewData(null)
//                Toast.makeText(this, "请稍等...", Toast.LENGTH_LONG).show()
//            }
//            Thread(Runnable {
//                selectList?.forEach {
//                    val file = File(it.path)
//                    val nsfwScore = file.getNsfwScore()
//                    addDataToAdapter(
//                        MyNsfwBean(
//                            nsfwScore.sfw,
//                            nsfwScore.nsfw,
//                            it.path,
//                            BitmapFactory.decodeStream(file.inputStream())
//                        )
//                    )
//                }
//            }).start()
//
//        }
//    }
}

