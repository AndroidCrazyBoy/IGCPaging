package com.igc.list.paging

import androidx.lifecycle.MutableLiveData
import com.igc.list.paging.PageList.Companion.FILTER_IGNORE
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

    private var pageIndex = INIT_PAGE_COUNT

    override fun dispatchLoadInitial(
        key: Key?,
        pageSize: Int,
        receiver: PageResult.Receiver<Value>
    ) {
        Logger.d("Paging ---->dispatchLoadInitial LOADING")
        refresh = {
            if (initialLoad.value != NetworkState.LOADING) {
                pageIndex = INIT_PAGE_COUNT
                initialLoad.value = NetworkState.LOADING
                loadMoreState.value = NetworkState.IDEAL
                loadInitial(
                    LoadParams(key, pageSize, pageIndex),
                    LoadCallbackImpl(PageResult.INIT, receiver)
                )
            }
        }
        refresh?.invoke()
        retry = refresh
    }

    override fun dispatchLoadAfter(key: Key?, pageSize: Int, receiver: PageResult.Receiver<Value>) {
        Logger.d("Paging ---->dispatchLoadAfter LOADING")
        // 加载完毕不需要进行分发下一页请求
        if (loadMoreState.value == NetworkState.COMPLETE || loadMoreState.value == NetworkState.COMPLETE_WITHOUT_TEXT || initialLoad.value == NetworkState.LOADING) {
            Logger.d("Paging ---->dispatchLoadAfter COMPLETE")
            return
        }

        when (loadMoreState.value?.status) {
            Status.IDEAL -> pageIndex = INIT_PAGE_COUNT + 1
            Status.FAILED -> pageIndex
            else -> pageIndex++
        }

        retry = {
            if (loadMoreState.value != NetworkState.LOADING) {
                loadMoreState.value = NetworkState.LOADING
                loadAfter(
                    LoadParams(key, pageSize, pageIndex),
                    LoadCallbackImpl(PageResult.APPEND, receiver)
                )
            }
        }.also { it.invoke() }
    }

    fun retry() {
        retry?.invoke()
    }

    fun refresh() {
        refresh?.invoke()
    }

    fun isInitial(): Boolean = initialLoad.value == NetworkState.IDEAL

    fun canLoadMore(): Boolean =
        (loadMoreState.value == NetworkState.IDEAL
                || loadMoreState.value == NetworkState.LOADED
                || loadMoreState.value?.status == Status.FAILED)
                && loadMoreState.value != NetworkState.COMPLETE
                && loadMoreState.value != NetworkState.COMPLETE_WITHOUT_TEXT

    /**
     * 不为空或[FILTER_IGNORE]，会自动去重
     * @param data 每一条数据
     * @return Long 回调返回一个整数，值相同则过滤，仅保留第一个
     */
    open fun filterDuplicatesCondition(): ((Value) -> Long)? {
        return null
    }

    abstract fun loadInitial(params: LoadParams, callback: LoadCallback<Value>)

    abstract fun loadAfter(params: LoadParams, callback: LoadCallback<Value>)

    inner class LoadParams(var key: Key?, val pageSize: Int, val pageIndex: Int)

    inner class LoadCallbackImpl<Value>(val type: Int, val receiver: PageResult.Receiver<Value>) :
        LoadCallback<Value> {
        override fun onFinish() {
            Logger.d("Paging ---->LoadCallbackImpl onFinish")
            receiver.onPageResult(PageResult.FINISHED, PageResult(Collections.emptyList()))
            initialLoad.value = NetworkState.COMPLETE
            loadMoreState.value = NetworkState.COMPLETE
        }

        override fun onFinishWithoutNoMoreData() {
            Logger.d("Paging ---->LoadCallbackImpl onFinishWithoutNoMoreData")
            receiver.onPageResult(PageResult.FINISHED, PageResult(Collections.emptyList()))
            initialLoad.value = NetworkState.COMPLETE
            loadMoreState.value = NetworkState.COMPLETE_WITHOUT_TEXT
        }

        override fun onResult(data: List<Value>) {
            Logger.d("Paging ---->LoadCallbackImpl LOADED size = " + data.size)
            receiver.onPageResult(type, PageResult(data))
            when (type) {
                PageResult.INIT -> initialLoad.value = NetworkState.LOADED
                PageResult.APPEND -> loadMoreState.value = NetworkState.LOADED
            }
        }

        override fun onError(error: Throwable) {
            Logger.d("Paging ---->LoadCallbackImpl onError =" + error.message)
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
         * 加载完成
         */
        fun onFinish()

        /**
         * 加载完成(没有加载完成的文案占位)
         */
        fun onFinishWithoutNoMoreData()
    }
}