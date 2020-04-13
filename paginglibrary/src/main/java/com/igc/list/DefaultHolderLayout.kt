package com.igc.list

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

/**
 * 默认layout占位
 * @author baolongxiang
 * @createTime 2019-08-05
 */
@SuppressLint("InflateParams")
open class DefaultHolderLayout(val appContext: Context) : ILoadMoreHolderLayout {
    override fun getLoadMoreLayout(parent: ViewGroup): View {
        return LayoutInflater.from(appContext).inflate(R.layout.paging_load_more, parent, false)
    }

    override fun getLoadFinishLayout(parent: ViewGroup): View {
        return LayoutInflater.from(appContext).inflate(R.layout.paging_load_finish, parent, false)
    }
}