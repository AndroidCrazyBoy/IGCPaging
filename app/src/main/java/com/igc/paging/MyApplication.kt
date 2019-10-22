package com.igc.list

import android.app.Application
import com.igc.list.paging.PageList
import com.orhanobut.logger.AndroidLogAdapter
import com.orhanobut.logger.Logger

/**
 * @author baolongxiang
 * @createTime 2019-08-18
 */
class MyApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        initLogger()

        initPagingConfig()
    }

    private fun initPagingConfig() {
        GlobalListInitializer.instance
                .setPagingConfig(PageList.Config(20, 25))
    }

    private fun initLogger() {
        Logger.addLogAdapter(object : AndroidLogAdapter() {
            override fun isLoggable(priority: Int, tag: String?): Boolean {
                return BuildConfig.DEBUG
            }
        })
    }
}