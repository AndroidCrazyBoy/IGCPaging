package com.igc.list.paging

import android.os.Handler
import android.os.Looper
import android.support.v7.util.DiffUtil
import android.support.v7.widget.RecyclerView
import com.igc.list.IPagingAdapter
import java.lang.Exception

/**
 * 辅助更新列表工具
 * @author baolongxiang
 * @createTime 2019-07-06
 */
class NotifyUtil(val adapter: IPagingAdapter) {
    private var pageList: PageList<*>? = null

    private var enableLoadMore: Boolean = true

    private val handler: Handler = Handler(Looper.getMainLooper())

    private val notifyCallback: PageList.NotifyCallback = object : PageList.NotifyCallback {
        override fun <T> onDataChange(oldData: List<T>, newData: List<T>) {
            try {
                // 新数据或老数据如果是空数据集，diffUtil会更新失败
                if (newData.isEmpty() || oldData.isEmpty()) {
                    adapter.notifyDataSetChanged()
                } else {
                    val diffResult = DiffUtil.calculateDiff(DefaultDiffCallBack(oldData, newData))
                    diffResult.dispatchUpdatesTo(adapter as RecyclerView.Adapter<*>)
                }
            } catch (e: Exception) {
                adapter.notifyDataSetChanged()
            }
        }
    }

    fun notifyItemInserted(position: Int) {
        handler.post {
            adapter.notifyItemInserted(position)
        }
    }

    fun notifyItemChanged(position: Int) {
        handler.post {
            adapter.notifyItemChanged(position)
        }
    }

    fun notifyItemRemoved(position: Int) {
        handler.post {
            adapter.notifyItemRemoved(position)
        }
    }

    fun getItemCount(): Int {
        return pageList?.size ?: 0
    }

    fun <T> submitList(pageList: PageList<T>, oldPageList: PageList<*>?) {
        this.pageList = pageList
        this.adapter.itemData = pageList
        pageList.addNotifyCallback(notifyCallback)
        oldPageList?.let {
            notifyCallback.onDataChange(it, pageList)
        }
    }

    fun itemLoadPosition(position: Int) {
        if (enableLoadMore) {
            pageList?.loadAround(position)
        }
    }

    fun enableLoadMore(enableLoadMore: Boolean) {
        this.enableLoadMore = enableLoadMore
    }

    private inner class DefaultDiffCallBack(var oldData: List<*>, var newData: List<*>) : DiffUtil.Callback() {
        override fun areItemsTheSame(oldPosition: Int, newPosition: Int): Boolean {
            adapter.getDiffCallback()?.let {
                return it.areItemsTheSame(oldPosition, newPosition)
            }
            return oldData[oldPosition] == newData[newPosition]
        }

        override fun getOldListSize(): Int {
            adapter.getDiffCallback()?.let {
                return it.getOldListSize()
            }
            return oldData.size
        }

        override fun getNewListSize(): Int {
            adapter.getDiffCallback()?.let {
                return it.getNewListSize()
            }
            return newData.size
        }

        override fun areContentsTheSame(oldPosition: Int, newPosition: Int): Boolean {
            adapter.getDiffCallback()?.let {
                return it.areContentsTheSame(oldPosition, newPosition)
            }
            return oldData[oldPosition] == newData[newPosition]
        }
    }
}