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
@SuppressLint("InflateParams")
open class DefaultHolderLayout(val appContext: Context) : ILoadMoreHolderLayout {
    private val loadMoreView: View by lazy { LayoutInflater.from(appContext).inflate(R.layout.paging_load_more, null, false) }
    private val loadFinishView: View by lazy { LayoutInflater.from(appContext).inflate(R.layout.paging_load_finish, null, false) }

    override fun getLoadMoreLayout(): View {
        return loadMoreView
    }

    override fun getLoadFinishLayout(): View {
        return loadFinishView
    }
}