package com.igc.list

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View

/**
 * 默认layout占位
 * @author baolongxiang
 * @createTime 2019-08-05
 */
open class DefaultHolderLayout(val appContext: Context) : ILoadMoreHolderLayout {

    @SuppressLint("InflateParams")
    override fun getLoadMoreLayout(): View {
        return LayoutInflater.from(appContext).inflate(R.layout.paging_load_more, null, false)
    }

    @SuppressLint("InflateParams")
    override fun getLoadFinishLayout(): View {
        return LayoutInflater.from(appContext).inflate(R.layout.paging_load_finish, null, false)
    }
}