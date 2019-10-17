package com.igc.list.paging

import android.arch.lifecycle.MutableLiveData
import com.orhanobut.logger.Logger
import java.util.*

/**
 * 数据分发类
 * @author baolongxiang
 * @createTime 2019-07-05
 */
abstract class PageKeyDataSource<Key, Value> : DataSource<Key, Value>() {

    companion object {
        private const val INIT_PAGE_COUNT = 1
    }

    val loadMoreState = MutableLiveData<NetworkState>()

    val initialLoad = MutableLiveData<NetworkState>().apply { value = NetworkState.IDEAL }

    private var refresh: (() -> Any)? = null

    private var retry: (() -> Any)? = null

    private var pageCount = INIT_PAGE_COUNT

    override fun dispatchLoadInitial(key: Key?, pageSize: Int, receiver: PageResult.Receiver<Value>) {
        Logger.d("TEST ---->dispatchLoadInitial LOADING")
        refresh = {
            if (initialLoad.value != NetworkState.LOADING) {
                pageCount = INIT_PAGE_COUNT
                initialLoad.value = NetworkState.LOADING
                loadMoreState.value = NetworkState.IDEAL
                loadInitial(LoadParams(key, pageSize, pageCount), LoadCallbackImpl(PageResult.INIT, receiver))
            }
        }
        refresh!!.invoke()
        retry = refresh
    }

    override fun dispatchLoadAfter(key: Key?, pageSize: Int, receiver: PageResult.Receiver<Value>) {
        Logger.d("TEST ---->dispatchLoadAfter LOADING")
        // 加载完毕不需要进行分发下一页请求
        if (loadMoreState.value == NetworkState.COMPLETE) {
            Logger.d("TEST ---->dispatchLoadAfter COMPLETE")
            return
        }
        pageCount++
        retry = {
            if (loadMoreState.value != NetworkState.LOADING) {
                loadMoreState.value = NetworkState.LOADING
                loadAfter(LoadParams(key, pageSize, pageCount), LoadCallbackImpl(PageResult.APPEND, receiver))
            }
        }
        retry!!.invoke()
    }

    fun retry() {
        retry?.invoke()
    }

    fun refresh() {
        refresh?.invoke()
    }

    fun isInitial(): Boolean = initialLoad.value == NetworkState.IDEAL

    fun canLoadMore(): Boolean =
        (loadMoreState.value == NetworkState.LOADED
                || loadMoreState.value == NetworkState.IDEAL
                || loadMoreState.value?.status == Status.FAILED)
                && loadMoreState.value != NetworkState.COMPLETE

    abstract fun loadInitial(params: LoadParams, callback: LoadCallback<Value>)

    abstract fun loadAfter(params: LoadParams, callback: LoadCallback<Value>)

    inner class LoadParams(var key: Key?, val pageSize: Int, val pageIndex: Int)

    inner class LoadCallbackImpl<Value>(val type: Int, val receiver: PageResult.Receiver<Value>) : LoadCallback<Value> {
        override fun onFinish(withoutHold: Boolean) {
            Logger.d("TEST ---->LoadCallbackImpl onFinish")
            initialLoad.value = NetworkState.COMPLETE
            if (withoutHold) {
                loadMoreState.value = NetworkState.COMPLETE_WITHOUT_TEXT
            } else {
                loadMoreState.value = NetworkState.COMPLETE
            }
            receiver.onPageResult(PageResult.FINISHED, PageResult(Collections.emptyList()))
        }

        override fun onResult(data: List<Value>) {
            when (type) {
                PageResult.INIT -> initialLoad.value = NetworkState.LOADED
                PageResult.APPEND -> loadMoreState.value = NetworkState.LOADED
            }
            Logger.d("TEST ---->LoadCallbackImpl LOADED size = " + data.size)
            receiver.onPageResult(type, PageResult(data))
        }

        override fun onError(error: Throwable) {
            pageCount--
            when (type) {
                PageResult.INIT -> initialLoad.value = NetworkState.error(error.message)
                PageResult.APPEND -> loadMoreState.value = NetworkState.error(error.message)
            }
        }
    }

    interface LoadCallback<Value> {
        /**
         * 返回结果
         */
        fun onResult(data: List<Value>)

        /**
         * 错误回调
         */
        fun onError(error: Throwable)

        /**
         * 加载完成(不在粗发上拉加载, 并且显示完成文案)
         * @param withoutHold 是否显示占位文本
         */
        fun onFinish(withoutHold: Boolean = false)
    }
}