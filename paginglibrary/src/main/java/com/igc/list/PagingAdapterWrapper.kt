package com.igc.list

import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.igc.list.paging.NetworkState
import com.igc.list.paging.NotifyUtil
import com.igc.list.paging.PageList
import com.orhanobut.logger.Logger
import kotlinx.android.synthetic.main.item_append.view.*
import java.util.*

/**
 * 分页加载adapter 包装类
 * @author baolongxiang
 * @createTime 2019-07-01
 */
class PagingAdapterWrapper(val adapter: IPagingAdapter) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        private const val TYPE_APPEND = 1001
    }

    /**
     * 刷新辅助类
     */
    private val notifyUtil by lazy { NotifyUtil(adapter) }

    /**
     * 上拉加载状态（依据此状态显示与隐藏加载更多的item）
     */
    private var loadMoreState: NetworkState = NetworkState.IDEAL

    /**
     * 加载更多itemView
     */
    private var loadMoreView: View? = null

    /**
     * 加载完成itemView
     */
    private var loadFinishView: View? = null

    /**
     * 是否可以进行上拉加载
     */
    private var enableLoadMore: Boolean = true

    /**
     * 记录旧数据配合diffUtil进行数据刷新
     */
    var oldItemDatas: PageList<*>? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == TYPE_APPEND) {
            AppendViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_append, parent, false))
        } else {
            adapter.onCreateViewHolder(parent, viewType)
        }
    }

    @Suppress("UNCHECKED_CAST")
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (getItemViewType(position) == TYPE_APPEND) {
            (holder as AppendViewHolder).bind(loadMoreState, loadMoreView, loadFinishView)
        } else {
            adapter.onBindViewHolder(holder, position, Collections.emptyList())
        }
        notifyUtil.itemLoadPosition(position)
    }

    override fun getItemCount(): Int {
        return notifyUtil.getItemCount() + if (hasExtraRow()) 1 else 0
    }

    fun <T> submitList(pageList: PageList<T>) {
        notifyUtil.submitList(pageList, oldItemDatas)
    }

    fun setLoadedState(state: NetworkState) {
        this.loadMoreState = state
        when (state) {
            NetworkState.LOADING -> {
                notifyUtil.notifyItemInserted(this.itemCount)
            }
            NetworkState.LOADED,
            NetworkState.COMPLETE -> {
                notifyUtil.notifyItemChanged(this.itemCount - 1)
            }
        }
    }

    fun setLoadMoreView(loadMoreView: View?) {
        this.loadMoreView = loadMoreView
    }

    fun setLoadFinishView(loadFinishView: View?) {
        this.loadFinishView = loadFinishView
    }

    private fun hasExtraRow(): Boolean {
        return enableLoadMore && notifyUtil.getItemCount() > 0
    }

    override fun getItemViewType(position: Int): Int {
        return if (hasExtraRow() && position == itemCount - 1) {
            TYPE_APPEND
        } else {
            adapter.getItemViewType(position)
        }
    }

    override fun registerAdapterDataObserver(observer: RecyclerView.AdapterDataObserver) {
        super.registerAdapterDataObserver(observer)
        adapter.registerAdapterDataObserver(observer)
    }

    override fun unregisterAdapterDataObserver(observer: RecyclerView.AdapterDataObserver) {
        super.unregisterAdapterDataObserver(observer)
        adapter.unregisterAdapterDataObserver(observer)
    }

    fun enableLoadMore(enableLoadMore: Boolean) {
        this.enableLoadMore = enableLoadMore
        notifyUtil.enableLoadMore(enableLoadMore)
    }

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        adapter.onAttachedToRecyclerView(recyclerView)
        val layoutManager = recyclerView.layoutManager
        if (layoutManager is GridLayoutManager) {
            val spanSizeLookup = layoutManager.spanSizeLookup
            layoutManager.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
                override fun getSpanSize(position: Int): Int {
                    return if (hasExtraRow() && position == itemCount - 1) {
                        layoutManager.spanCount
                    } else {
                        spanSizeLookup.getSpanSize(position)
                    }
                }
            }
        }
    }

    override fun onDetachedFromRecyclerView(recyclerView: RecyclerView) {
        adapter.onDetachedFromRecyclerView(recyclerView)
    }

    override fun onViewAttachedToWindow(holder: RecyclerView.ViewHolder) {
        if (hasExtraRow()) {
            setFullSpan(holder)
        } else {
            adapter.onViewAttachedToWindow(holder)
        }
    }

    private fun setFullSpan(holder: RecyclerView.ViewHolder) {
        val lp: ViewGroup.LayoutParams? = holder.itemView.layoutParams
        if (lp != null && lp is android.support.v7.widget.StaggeredGridLayoutManager.LayoutParams) {
            lp.isFullSpan = true
        }
    }

    /**
     * 加载更多和完成的item ViewHolder
     */
    private inner class AppendViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
        fun bind(state: NetworkState, loadMoreView: View?, loadFinishView: View?) {
            val container = view.container
            loadMoreView?.let {
                if (it.parent != null) {
                    (it.parent as ViewGroup).removeView(it)
                }
                container.addView(loadMoreView)
            }

            loadFinishView?.let {
                if (it.parent != null) {
                    (it.parent as ViewGroup).removeView(it)
                }
                container.addView(loadFinishView)
            }

            Logger.d("TEST ----> AppendViewHolder state =" + state)

            when (state) {
                NetworkState.LOADED,
                NetworkState.COMPLETE_WITHOUT_TEXT -> {
                    loadMoreView?.visibility = View.GONE
                    loadFinishView?.visibility = View.GONE
                }
                NetworkState.IDEAL,
                NetworkState.LOADING -> {
                    loadMoreView?.visibility = View.VISIBLE
                    loadFinishView?.visibility = View.GONE
                }
                NetworkState.COMPLETE -> {
                    loadMoreView?.visibility = View.GONE
                    loadFinishView?.visibility = View.VISIBLE
                }
                else -> {
                    loadMoreView?.visibility = View.VISIBLE
                    loadFinishView?.visibility = View.GONE
                }
            }
        }
    }
}