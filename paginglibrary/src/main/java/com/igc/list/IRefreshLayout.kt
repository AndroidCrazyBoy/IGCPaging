package com.igc.list

/**
 * @author baolongxiang
 * @createTime 2019-09-23
 */
interface IRefreshLayout {

    /**
     * 显示刷新动画，触发事件
     */
    fun autoRefresh(): Boolean

    fun finishPullRefresh()

    fun setOnPullRefreshListener(listener: PullRefreshListener)

    interface PullRefreshListener {
        fun onRefresh(refreshLayout: IRefreshLayout)
    }
}