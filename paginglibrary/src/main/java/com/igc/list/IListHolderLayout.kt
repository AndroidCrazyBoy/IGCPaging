package com.igc.list

import android.view.View

/**
 * 上拉刷新和加载的itemView
 * @author baolongxiang
 * @createTime 2019-08-05
 */
interface IListHolderLayout {

    fun getLoadMoreLayout(): View

    fun getLoadFinishLayout(): View
}