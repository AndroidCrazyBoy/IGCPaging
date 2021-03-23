package com.igc.list

import android.content.Context
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.*
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.igc.list.paging.NetworkState
import com.igc.list.paging.PageList
import com.igc.list.paging.Status
import com.orhanobut.logger.Logger

/**
 * recyclerView 管理器
 * @author baolongxiang
 * @createTime 2019-07-01
 */
class ListManager(private val builder: Builder) : ViewModel(), IRefreshLayout.PullRefreshListener {
    private var listing: Listing<Any>? = null

    init {
        val adapter1 = builder.adapter
        val recyclerView = builder.recyclerView
        if (adapter1 == null || recyclerView == null) {
            throw NullPointerException("ListManager adapter or recyclerView must not be null")
        }
        recyclerView.layoutManager = builder.layoutManager ?: LinearLayoutManager(builder.context)
        recyclerView.adapter = PagingAdapterWrapper(adapter1)
        // 是否显示默认刷新动画
        recyclerView.itemAnimator = if (builder.enableNotifyAnim) DefaultItemAnimator() else null
        // 上拉加载
        val adapter = recyclerView.adapter as PagingAdapterWrapper
        adapter.enableLoadMore(builder.enableLoadMore)
        if (builder.enableLoadMore) {
            adapter.setLoadMoreView(builder.layoutHolder?.getLoadMoreLayout(recyclerView))
            adapter.setLoadFinishView(builder.layoutHolder?.getLoadFinishLayout(recyclerView))
        }
        // 下拉刷新
        builder.refreshLayout?.setOnPullRefreshListener(this)
        // 绑定listing（数据及状态）
        builder.listing?.let {
            bindPageList(it)
        }
    }

    /**
     * 给recyclerview设置数据 <===> adapter.setData
     */
    private fun submitList(pageList: PageList<Any>?) {
        if (pageList == null) {
            Logger.e("ListManager submitList -> pageList is null")
            return
        }
        if (builder.recyclerView?.adapter is PagingAdapterWrapper) {
            (builder.recyclerView?.adapter as PagingAdapterWrapper).submitList(pageList)
        }
    }

    /**
     * 设置上拉加载的状态
     */
    private fun setLoadedState(state: NetworkState?) {
        if (state == null) {
            Logger.e("ListManager setLoadedState -> state is null")
            return
        }
        if (builder.recyclerView?.adapter is PagingAdapterWrapper) {
            (builder.recyclerView?.adapter as PagingAdapterWrapper).setLoadedState(state)
        }
    }

    /**
     * 将listing与ListManager绑定
     */
    fun bindPageList(listing: Listing<Any>) {
        this.listing = listing
        listing.pagedList?.observe(builder.lifecycleOwner, Observer {
            submitList(it)
        })
        listing.loadMoreState?.observe(builder.lifecycleOwner, Observer {
            setLoadedState(it)
        })
        listing.refreshState?.observe(builder.lifecycleOwner, Observer {
            it ?: return@Observer
            when (it.status) {
                Status.SUCCESS,
                Status.FAILED -> {
                    builder.refreshLayout?.finishPullRefresh()
                }
                else -> {
                }
            }
        })
    }

    fun changePageList(
        onlySizeChange: Boolean = false,
        block: (old: PageList<Any>?) -> PageList<Any>?
    ) {
        val pageList = listing?.pagedList?.value
        val oldPageList = pageList?.copyPageList(deepCopy = !onlySizeChange)
        // 记录旧数据配合diffUtil进行数据刷新
        (builder.recyclerView?.adapter as? PagingAdapterWrapper)?.oldItemDatas = oldPageList
        listing?.pagedList?.value = block.invoke(pageList)
    }

    override fun onRefresh(refreshLayout: IRefreshLayout) {
        refresh()
        builder.refreshListener?.onRefresh(refreshLayout)
    }

    /**
     * 重试（记录的是最后一次网络请求）
     */
    fun retry() {
        checkNotNull(listing) {
            "you must be call bindWith() before retry()"
        }
        listing?.retry?.invoke()
    }

    /**
     * 首屏数据获取（下拉刷新）
     */
    fun refresh() {
        checkNotNull(listing) {
            "ListManager you must be call bindWith() before refresh()"
        }
        listing?.refresh?.invoke()
    }

    @Deprecated("rename", ReplaceWith("observeRefreshState(block)"))
    fun getRefreshState(block: (state: NetworkState?) -> Unit) {
        observeRefreshState(block)
    }

    @Deprecated("rename", ReplaceWith("observeLoadMoreState(block)"))
    fun getLoadMoreState(block: (state: NetworkState?) -> Unit) {
        observeLoadMoreState(block)
    }

    /**
     * 获取加载更多的状态信息(监听到一次结果（success or fail）后停止监听)
     */
    fun getRefreshResultStateOnce(block: (state: NetworkState?) -> Unit) {
        listing?.refreshState?.observeResultOnce(builder.lifecycleOwner, Observer {
            block.invoke(it)
        })
    }

    /**
     * 获取下拉刷新的状态信息(监听到一次结果（success or fail）后停止监听)
     */
    fun getLoadMoreResultStateOnce(block: (state: NetworkState?) -> Unit) {
        listing?.loadMoreState?.observeResultOnce(builder.lifecycleOwner, Observer {
            block.invoke(it)
        })
    }

    /**
     * 获取下拉刷新的状态信息
     */
    fun observeRefreshState(block: (state: NetworkState?) -> Unit) {
        listing?.refreshState?.observe(builder.lifecycleOwner, Observer {
            block.invoke(it)
        })
    }

    /**
     * 获取加载更多的状态信息
     */
    fun observeLoadMoreState(block: (state: NetworkState?) -> Unit) {
        listing?.loadMoreState?.observe(builder.lifecycleOwner, Observer {
            block.invoke(it)
        })
    }

    /**
     * 重置上拉加载状态。
     */
    fun resetLoadMoreState() {
        listing?.resetLoadMoreState()
    }

    /**
     * 网络变化后自动重试
     */
    fun onNetWorkChangedDoRetry(change: Boolean) {
        val loadMoreError = listing?.loadMoreState?.value?.status == Status.FAILED
        val refreshError = listing?.refreshState?.value?.status == Status.FAILED
        if (change && (loadMoreError || refreshError)) {
            retry()
        }
    }

    override fun onCleared() {
        super.onCleared()
        listing?.destroy?.invoke()
    }

    @Suppress("UNCHECKED_CAST")
    class Builder {
        internal var recyclerView: RecyclerView? = null
        internal var refreshLayout: IRefreshLayout? = null
        internal var refreshListener: IRefreshLayout.PullRefreshListener? = null
        internal var adapter: IPagingAdapter? = null
        internal var context: Context? = null
        internal var layoutManager: RecyclerView.LayoutManager? = null
        internal var listing: Listing<Any>? = null

        /**
         * 是否显示recyclerview默认刷新动画
         */
        internal var enableNotifyAnim: Boolean = true

        /**
         * 是否需要上拉加载
         */
        internal var enableLoadMore: Boolean = true

        /**
         * 加载更多和下拉刷新样式
         */
        internal var layoutHolder: ILoadMoreHolderLayout? = null
        internal lateinit var lifecycleOwner: LifecycleOwner
        fun setAdapter(adapter: IPagingAdapter): Builder {
            this.adapter = adapter
            return this
        }

        fun setLayoutManager(layoutManager: RecyclerView.LayoutManager): Builder {
            this.layoutManager = layoutManager
            return this
        }

        fun enableLoadMore(enable: Boolean): Builder {
            this.enableLoadMore = enable
            return this
        }

        fun enableNotifyAnim(enable: Boolean): Builder {
            this.enableNotifyAnim = enable
            return this
        }

        fun setLoadMoreHolderLayout(layout: ILoadMoreHolderLayout): Builder {
            this.layoutHolder = layout
            return this
        }

        fun into(
            recyclerView: RecyclerView,
            refreshLayout: IRefreshLayout? = null,
            refreshListener: IRefreshLayout.PullRefreshListener? = null
        ): Builder {
            this.recyclerView = recyclerView
            this.refreshLayout = refreshLayout
            this.refreshListener = refreshListener
            return this
        }

        fun <T> bindPageList(listing: Listing<T>?): Builder {
            this.listing = listing as Listing<Any>
            return this
        }

        fun build(activity: FragmentActivity): ListManager {
            this.lifecycleOwner = activity
            this.context = activity
            this.layoutHolder = if (layoutHolder == null)
                GlobalListInitializer.instance.getListHolderLayout(activity)
            else
                layoutHolder
            return ViewModelProviders.of(activity, object : ViewModelProvider.Factory {
                override fun <T : ViewModel?> create(modelClass: Class<T>): T {
                    return ListManager(this@Builder) as T
                }
            })[ListManager::class.java]
        }

        fun build(fragment: Fragment, context: Context): ListManager {
            this.lifecycleOwner = fragment
            this.context = context
            this.layoutHolder =
                if (layoutHolder == null) GlobalListInitializer.instance.getListHolderLayout(context) else layoutHolder
            return ViewModelProviders.of(fragment, object : ViewModelProvider.Factory {
                override fun <T : ViewModel?> create(modelClass: Class<T>): T {
                    return ListManager(this@Builder) as T
                }
            })[ListManager::class.java]
        }
    }

    /**
     * 只监听结果状态(监听到一次结果（success or fail）后停止监听)
     */
    private fun LiveData<NetworkState>.observeResultOnce(
        lifecycleOwner: LifecycleOwner,
        observer: Observer<NetworkState>
    ) {
        observe(lifecycleOwner, object : Observer<NetworkState> {
            override fun onChanged(state: NetworkState?) {
                observer.onChanged(state)
                if (state?.status == Status.SUCCESS || state?.status == Status.FAILED) {
                    removeObserver(this)
                }
            }
        })
    }
}