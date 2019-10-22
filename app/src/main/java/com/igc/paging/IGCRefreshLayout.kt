package com.igc.list

import android.content.Context
import android.util.AttributeSet
import com.scwang.smartrefresh.layout.SmartRefreshLayout
import com.scwang.smartrefresh.layout.api.RefreshLayout
import com.scwang.smartrefresh.layout.header.ClassicsHeader
import com.scwang.smartrefresh.layout.listener.OnRefreshListener

/**
 * @author baolongxiang
 * @createTime 2019-09-23
 */
class IGCRefreshLayout @JvmOverloads constructor(
        context: Context,
        attrs: AttributeSet? = null,
        defStyleAttr: Int = 0
) : SmartRefreshLayout(context, attrs, defStyleAttr), IRefreshLayout, OnRefreshListener {

    private var refreshListener: IRefreshLayout.PullRefreshListener? = null

    init {
        setRefreshHeader(ClassicsHeader(context))
        setEnableRefresh(true)
        setEnableLoadMore(false)
        setOnRefreshListener(this)
    }

    override fun onRefresh(refreshLayout: RefreshLayout) {
        refreshListener?.onRefresh(this)
    }

    override fun finishPullRefresh() {
        finishRefresh()
    }

    override fun setOnPullRefreshListener(listener: IRefreshLayout.PullRefreshListener) {
        this.refreshListener = listener
    }
}