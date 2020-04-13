package com.igc.list

import android.view.View
import android.view.ViewGroup

/**
 * 上拉刷新和加载的itemView
 * @author baolongxiang
 * @createTime 2019-08-05
 */
interface ILoadMoreHolderLayout {

    fun getLoadMoreLayout(parent: ViewGroup): View

    fun getLoadFinishLayout(parent: ViewGroup): View
}