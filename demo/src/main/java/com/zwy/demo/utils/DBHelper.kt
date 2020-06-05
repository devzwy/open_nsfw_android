package com.zwy.demo.utils

import com.zwy.demo.NSFWApplication
import com.zwy.demo.db.DaoMaster
import com.zwy.demo.db.DaoSession
import com.zwy.demo.dbbean.HomeTitle


/**
 * 数据库操作类
 */
class DBHelper private constructor(val mDaoSession: DaoSession) {

    /**
     * 获取全部已缓存的图片列表
     */
    fun getImageListFromDB() = mDaoSession.imageBeanDao.queryBuilder().list()

    /**
     * 获取首页列表数据
     */
    fun getTitles() = mDaoSession.homeTitleDao.queryBuilder().list()

    companion object {
        private var instance: DBHelper? = null
            get() {
                if (field == null) {
                    field = DBHelper(initDB())
                }
                return field
            }

        private fun initDB(): DaoSession {
            return DaoMaster(
                DaoMaster.DevOpenHelper(
                    NSFWApplication.context,
                    "nsfw_db"
                ).writableDatabase
            ).newSession().apply {
                //插入默认数据
                if (this.homeTitleDao.queryBuilder().list().size == 0) {
                    arrayListOf(
                        "识别Assets目录图片",
                        "识别相册图片",
                        "实时扫描识别",
                        "测试网络图片库中的大尺度资源",
                        "我有资源贡献",
                        "如果项目对您有帮助",
                        "请给我一个Star吧"
                    ).forEach {
                        System.out.println("数据写入成功")
                        this.homeTitleDao.insert(HomeTitle(it))
                    }
                }
            }
        }

        @Synchronized
        fun get(): DBHelper {
            return instance!!
        }
    }

}