package com.example.open_nsfw_android.view

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.widget.Toast
import com.bumptech.glide.Glide
import com.example.open_nsfw_android.R
import com.example.open_nsfw_android.util.MainAdapter
import com.example.open_nsfw_android.util.MyNsfwBean
import com.example.open_nsfw_android.util.PackageUtils
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.luck.picture.lib.PictureSelector
import com.luck.picture.lib.config.PictureConfig
import com.luck.picture.lib.config.PictureMimeType
import com.luck.picture.lib.entity.LocalMedia
import getNsfwScore
import kotlinx.android.synthetic.main.activity_main.*
import okhttp3.*
import org.koin.android.ext.android.inject
import java.io.File
import java.io.IOException
import java.lang.reflect.Type

class MainAty : AppCompatActivity() {

    var selectList: List<LocalMedia>? = null

    lateinit var mMainAdapter: MainAdapter

    val httpClient: OkHttpClient by inject()

    val gson: Gson by inject()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        init()

    }


    @SuppressLint("SetTextI18n")
    fun init() {
        //检测权限
        checkPermissions()
        //初始化适配器
        initAdapter()
        //识别assets目录
        bt_sc_assets.setOnClickListener { assetsListSc() }
        //选择相册进行扫描
        bt_sc_from_other.setOnClickListener { selectImgFromD(0x01) }
        //跳转网络图片识别页面
        bt_sc_from_internet.setOnClickListener {
            startActivity(
                Intent(
                    this,
                    Main2Activity::class.java
                )
            )
        }
        //实时扫描
        bt_sc_from_cam.setOnClickListener { scCamera() }
        //调用Api获取测试图片
        bt_sc_from_api.setOnClickListener {
            httpClient.newCall(Request.Builder().get().url(GetImageListURL).build())
                .enqueue(object : Callback {
                    override fun onFailure(call: Call, e: IOException) {
                        runOnUiThread {
                            Toast.makeText(this@MainAty, "请求失败，请重试", Toast.LENGTH_SHORT).show()
                        }
                    }

                    override fun onResponse(call: Call, response: Response) {
                        val str = response.body?.string()
                        val type: Type? = object : TypeToken<List<NSFWImage>>() {}.type
                        val list = gson.fromJson<List<NSFWImage>>(str, type)
                        val l = ArrayList<MyNsfwBean>()
                        //迭代数据并获取涉黄程度
                        list.forEach {
                            System.out.println(it)
                            val file = Glide.with(this@MainAty)
                                .load(it.imgUrl)
                                .downloadOnly(120, 120).get()
                            val nsfwBean = file.getNsfwScore()
                            //填充数据到适配器
                            l.add(
                                MyNsfwBean(
                                    sfw = nsfwBean.sfw,
                                    nsfw = nsfwBean.nsfw,
                                    path = it.createTime,
                                    imgUrl = it.imgUrl,
                                    bitmap = null
                                )
                            )
                        }
                        runOnUiThread {
                            mMainAdapter.setNewData(null)
                            mMainAdapter.setNewData(l)
                        }
                    }
                })
        }

        bt_upload.setOnClickListener {
            selectImgFromD(0x02)
        }
        tv_version.text = "当前版本号：${PackageUtils.getVersionName(this)}"
    }


    /**
     * 实时扫描
     */
    private fun scCamera() {

        startActivity(Intent(this, CameraActivity::class.java))
    }


    private fun initAdapter() {
        mMainAdapter = MainAdapter(null)
        rv.layoutManager = LinearLayoutManager(this)
        rv.adapter = mMainAdapter
    }

    /**
     * 相册选择
     */
    private fun selectImgFromD(requestCode: Int) {
        PictureSelector.create(this)
            .openGallery(PictureMimeType.ofImage())//全部.ofAll()、图片.、视频.ofVideo()、音频.ofAudio()
            .maxSelectNum(20)// 最大图片选择数量 int
            .minSelectNum(1)// 最小选择数量 int
            .imageSpanCount(3)// 每行显示个数 int
            .selectionMode(PictureConfig.MULTIPLE)// 多选 or 单选  or PictureConfig.SINGLE
            .previewImage(true)// 是否可预览图片 true or false
            .isCamera(false)// 是否显示拍照按钮 true or false
            .isZoomAnim(true)// 图片列表点击 缩放效果 默认true
            .selectionMedia(selectList)
            .sizeMultiplier(0.5f)// glide 加载图片大小 0~1之间 如设置 .glideOverride()无效
            .previewEggs(true)// 预览图片时 是否增强左右滑动图片体验(图片滑动一半即可看到上一张是否选中) true or false
            .forResult(requestCode);//结果回调onActivityResult code }
    }

    /**
     * 扫描assets目录
     */
    private fun assetsListSc() {
        mMainAdapter.setNewData(null)
        Toast.makeText(this, "请稍等...", Toast.LENGTH_LONG).show()
        Thread(Runnable {
            resources.assets.list("img")?.forEach {
                val bitmap = BitmapFactory.decodeStream(resources.assets.open("img/$it"))
                val nsfwScore = bitmap.getNsfwScore()
                addDataToAdapter(
                    MyNsfwBean(
                        nsfwScore.sfw,
                        nsfwScore.nsfw,
                        "img/$it",
                        bitmap
                    )
                )
            }
        }).start()
    }


    private fun addDataToAdapter(mMyNsfwBean: MyNsfwBean) {
        runOnUiThread { mMainAdapter.addData(mMyNsfwBean) }
    }

    /**
     * 检测权限
     */
    private fun checkPermissions() {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                1
            );
        }

        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.CAMERA
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CAMERA), 1);
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 0x01 && resultCode == RESULT_OK) {
            selectList = PictureSelector.obtainMultipleResult(data)
            selectList?.let {
                mMainAdapter.setNewData(null)
                Toast.makeText(this, "请稍等...", Toast.LENGTH_LONG).show()
            }
            Thread(Runnable {
                selectList?.forEach {
                    val file = File(it.path)
                    val nsfwScore = file.getNsfwScore()
                    addDataToAdapter(
                        MyNsfwBean(
                            nsfwScore.sfw,
                            nsfwScore.nsfw,
                            it.path,
                            BitmapFactory.decodeStream(file.inputStream())
                        )
                    )
                }
            }).start()

        }
        if (requestCode == 0x02 && resultCode == RESULT_OK) {
            selectList = PictureSelector.obtainMultipleResult(data)
            val builder = MultipartBody.Builder().setType(MultipartBody.FORM)
//            selectList?.let {
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

        }
    }

}