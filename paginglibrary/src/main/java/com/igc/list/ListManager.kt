package com.igc.list

import android.arch.lifecycle.*
import android.content.Context
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import com.igc.list.paging.NetworkState
import com.igc.list.paging.PageList
import com.igc.list.paging.Status
import com.orhanobut.logger.Logger

/**
 * recyclerView 管理器
 * @author baolongxiang
 * @createTime 2019-07-01
 */
@Suppress("UNCHECKED_CAST")
class ListManager(private val builder: Builder) : ViewModel(),
        IRefreshLayout.PullRefreshListener {

    private var listing: Listing<Any>? = null

    init {
        if (builder.adapter == null || builder.recyclerView == null) {
            throw NullPointerException("ListManager adapter or recyclerView must not be null")
        }
        builder.recyclerView!!.layoutManager = builder.layoutManager
                ?: LinearLayoutManager(builder.context)
        builder.recyclerView!!.adapter = PagingAdapterWrapper(builder.adapter!!)
        // 上拉加载
        val adapter = builder.recyclerView!!.adapter as PagingAdapterWrapper
        adapter.enableLoadMore(builder.enableLoadMore)
        if (builder.enableLoadMore) {
            adapter.setLoadMoreView(builder.layoutHolder?.getLoadMoreLayout())
            adapter.setLoadFinishView(builder.layoutHolder?.getLoadFinishLayout())
        }
        // 下拉刷新
        if (builder.refreshLayout != null) {
            val refreshLayout = builder.refreshLayout!!
            refreshLayout.setOnPullRefreshListener(this)
        }
        // 绑定listing（数据及状态）
        if (builder.listing != null) {
            bindPageList(builder.listing!!)
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
                Status.COMPLETE,
                Status.FAILED -> {
                    builder.refreshLayout?.finishPullRefresh()
                }
                else -> {
                }
            }
        })
    }

    fun changePageList(block: (old: PageList<Any>?) -> PageList<Any>?) {
        // 记录旧数据配合diffUtil进行数据刷新
        if (builder.recyclerView?.adapter is PagingAdapterWrapper) {
            (builder.recyclerView?.adapter as PagingAdapterWrapper).oldItemDatas = listing?.pagedList?.value?.copyPageList()
        }
        listing?.pagedList?.value = block.invoke(listing?.pagedList?.value)
    }

    override fun onRefresh(refreshLayout: IRefreshLayout) {
        refresh()
    }

    /**
     * 重试（记录的是最后一次网络请求）
     */
    fun retry() {
        if (listing == null) {
            throw java.lang.NullPointerException("you must be call bindWith() before retry()")
        }
        listing!!.retry()
    }

    /**
     * 首屏数据获取（下拉刷新）
     */
    fun refresh() {
        if (listing == null) {
            throw java.lang.NullPointerException("ListManager you must be call bindWith() before refresh()")
        }
        listing!!.refresh()
    }

    /**
     * 获取加载更多的状态信息
     */
    fun getLoadMoreState(block: (state: NetworkState?) -> Unit) {
        listing?.loadMoreState?.observe(builder.lifecycleOwner, Observer {
            block.invoke(it)
        })
    }

    /**
     * 获取下拉刷新的状态信息
     */
    fun getRefreshState(block: (state: NetworkState?) -> Unit) {
        listing?.refreshState?.observe(builder.lifecycleOwner, Observer {
            block.invoke(it)
        })
    }

    class Builder {

        internal var recyclerView: RecyclerView? = null

        internal var refreshLayout: IRefreshLayout? = null

        internal var adapter: IPagingAdapter? = null

        internal var context: Context? = null

        internal var layoutManager: RecyclerView.LayoutManager? = null

        internal var listing: Listing<Any>? = null

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

        fun setLoadMoreHolderLayout(layout: ILoadMoreHolderLayout): Builder {
            this.layoutHolder = layout
            return this
        }

        fun into(recyclerView: RecyclerView, refreshLayout: IRefreshLayout? = null): Builder {
            this.recyclerView = recyclerView
            this.refreshLayout = refreshLayout
            return this
        }

        fun <T : Any> bindPageList(listing: Listing<T>?): Builder {
            this.listing = listing as Listing<Any>
            return this
        }

        fun build(activity: FragmentActivity): ListManager {
            this.lifecycleOwner = activity
            this.context = activity
            this.layoutHolder = if (layoutHolder == null) GlobalListInitializer.instance.getListHolderLayout(context!!) else layoutHolder
            return ViewModelProviders.of(activity, object : ViewModelProvider.Factory {
                override fun <T : ViewModel?> create(modelClass: Class<T>): T {
                    return ListManager(this@Builder) as T
                }
            })[ListManager::class.java]
        }

        fun build(fragment: Fragment): ListManager {
            this.lifecycleOwner = fragment
            this.context = fragment.context
            this.layoutHolder = GlobalListInitializer.instance.getListHolderLayout(context!!)
            return ViewModelProviders.of(fragment, object : ViewModelProvider.Factory {
                override fun <T : ViewModel?> create(modelClass: Class<T>): T {
                    return ListManager(this@Builder) as T
                }
            })[ListManager::class.java]
        }
    }
}