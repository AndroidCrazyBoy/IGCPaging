package com.igc.list.paging

import android.os.Handler
import android.os.Looper
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.igc.list.IPagingAdapter
import com.orhanobut.logger.Logger

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
                Logger.e(e, "Paging NotifyUtil")
                postNotifyDataAdapter()
            }
        }
    }

    /**
     * 防止 Cannot call this method while RecyclerView is computing a layout or scrolling 导致崩溃
     */
    private fun postNotifyDataAdapter() {
        handler.post {
            adapter.notifyDataSetChanged()
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
        this.pageList?.resetNotifyCallback()
        this.pageList = pageList
        this.pageList?.setNotifyCallback(notifyCallback)
        this.adapter.itemData = pageList
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

    private inner class DefaultDiffCallBack(var oldData: List<*>, var newData: List<*>) :
        DiffUtil.Callback() {
        override fun getOldListSize(): Int {
            return oldData.size
        }

        override fun getNewListSize(): Int {
            return newData.size
        }

        override fun areItemsTheSame(oldPosition: Int, newPosition: Int): Boolean {
            adapter.getDiffCallback()?.let {
                return it.areItemsTheSame(oldData[oldPosition], newData[newPosition])
            }
            return oldData[oldPosition] == newData[newPosition]
        }

        override fun areContentsTheSame(oldPosition: Int, newPosition: Int): Boolean {
            adapter.getDiffCallback()?.let {
                return it.areContentsTheSame(oldData[oldPosition], newData[newPosition])
            }
            return oldData[oldPosition] == newData[newPosition]
        }

        override fun getChangePayload(oldItemPosition: Int, newItemPosition: Int): Any? {
            adapter.getDiffCallback()?.let {
                return it.getChangePayload(oldData[oldItemPosition], newData[newItemPosition])
            }
            return super.getChangePayload(oldItemPosition, newItemPosition)
        }
    }
}