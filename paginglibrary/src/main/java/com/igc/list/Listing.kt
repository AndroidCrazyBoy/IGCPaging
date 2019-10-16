package com.igc.list

import android.arch.lifecycle.MutableLiveData
import android.support.annotation.Keep
import com.igc.list.paging.NetworkState
import com.igc.list.paging.PageList

/**
 * class that is necessary for a UI to show a listing
 */
@Keep
class Listing<T>(
    /**
     * list数据
     */
    val pagedList: MutableLiveData<PageList<T>>?,

    /**
     * 上拉加载状态
     */
    val loadMoreState: MutableLiveData<NetworkState>?,

    /**
     * 下拉刷新状态
     */
    val refreshState: MutableLiveData<NetworkState>?,

    /**
     * 初始化首屏数据和下拉刷新
     */
    val refresh: (() -> Any),

    /**
     * 重试最后一次的网络请求
     */
    val retry: (() -> Any)
)