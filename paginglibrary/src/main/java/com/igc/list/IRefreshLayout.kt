package com.igc.list

/**
 * @author baolongxiang
 * @createTime 2019-09-23
 */
interface IRefreshLayout {

    fun autoRefresh(): Boolean

    fun finishPullRefresh()

    fun setOnPullRefreshListener(listener: PullRefreshListener)

    interface PullRefreshListener {
        fun onRefresh()
    }
}