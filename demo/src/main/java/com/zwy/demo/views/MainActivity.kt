package com.zwy.demo.views

import android.app.Activity
import android.os.Bundle
import com.zwy.demo.R
import com.zwy.demo.base.BaseActivity
import com.zwy.demo.databinding.MainLayoutBinding
import com.zwy.demo.models.MainViewModel
import d
import org.jetbrains.anko.toast
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream

class MainActivity : BaseActivity<MainLayoutBinding, MainViewModel>() {
    /**
     * 初始化根布局
     *
     * @return 布局layout的id
     */
    override fun initContentView(savedInstanceState: Bundle?): Int = R.layout.activity_main


    override fun initData() {
        binding.titles = viewModel.titles
        viewModel.getTitles()
//        copyAssetsFile2Phone(this,"nsfw.tflite")
    }

    private var mExitTime: Long = 0

    override fun onBackPressed() {
        if (System.currentTimeMillis() - mExitTime < 2000) {
            super.onBackPressed();
        } else {
            mExitTime = System.currentTimeMillis();
            toast("再按一次返回键退出应用")
        }
    }


//    /**
//     * 将文件从assets目录，考贝到 /data/data/包名/files/ 目录中。assets 目录中的文件，会不经压缩打包至APK包中，使用时还应从apk包中导出来
//     * @param fileName 文件名,如aaa.txt
//     */
//    fun copyAssetsFile2Phone(activity: Activity, fileName: String) {
//        try {
//            val inputStream: InputStream = activity.assets.open(fileName)
//            //getFilesDir() 获得当前APP的安装路径 /data/data/包名/files 目录
//            val file = File(
//                activity.filesDir.absolutePath + File.separator.toString() + fileName
//            )
//            if (!file.exists() || file.length()==0L) {
//                val fos = FileOutputStream(file) //如果文件不存在，FileOutputStream会自动创建文件
//                var len = -1
//                val buffer = ByteArray(1024)
//                while (inputStream.read(buffer).also({ len = it }) != -1) {
//                    fos.write(buffer, 0, len)
//                }
//                fos.flush() //刷新缓存区
//                inputStream.close()
//                fos.close()
//                "模型文件复制完毕".d()
//            } else {
//                "模型文件已存在，无需复制".d()
//            }
//        } catch (e: IOException) {
//            e.printStackTrace()
//        }
//    }


}
