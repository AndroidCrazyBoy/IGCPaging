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
    private var parent: ViewGroup? = null
    private val loadMoreView: View by lazy {
        LayoutInflater.from(appContext).inflate(R.layout.paging_load_more, parent, false)
    }
    private val loadFinishView: View by lazy {
        LayoutInflater.from(appContext).inflate(R.layout.paging_load_finish, parent, false)
    }

    override fun getLoadMoreLayout(parent: ViewGroup): View {
        this.parent = parent
        return loadMoreView
    }

    override fun getLoadFinishLayout(parent: ViewGroup): View {
        this.parent = parent
        return loadFinishView
    }
}