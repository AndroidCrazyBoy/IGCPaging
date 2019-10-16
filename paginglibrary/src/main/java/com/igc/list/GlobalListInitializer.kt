package com.igc.list

import android.content.Context
import com.igc.list.paging.PageList

/**
 * 全局设置 相关list的配置
 * @author baolongxiang
 * @createTime 2019-08-06
 */
class GlobalListInitializer {

    companion object {
        val instance: GlobalListInitializer by lazy { GlobalListInitializer() }
    }

    /**
     * 上拉加载和下拉刷新占位item
     */
    private var holderLayout: IListHolderLayout? = null

    /**
     * 分页配置
     */
    private var pagingConfig: PageList.Config? = null

    /**
     * 设置全局上拉加载和下拉刷新样式
     */
    fun setListHolderLayout(layout: IListHolderLayout): GlobalListInitializer {
        this.holderLayout = layout
        return instance
    }

    /**
     * 设置全局分页配置
     */
    fun setPagingConfig(config: PageList.Config): GlobalListInitializer {
        this.pagingConfig = config
        return instance
    }

    /**
     * 获取上拉加载和下拉刷新样式Holder
     */
    fun getListHolderLayout(context: Context): IListHolderLayout {
        return holderLayout ?: DefaultHolderLayout(context)
    }

    /**
     * 获取分页配置
     */
    fun getPagingConfig(): PageList.Config {
        return pagingConfig ?: PageList.Config.defaultConfig
    }
}