package com.igc.list

import android.support.v7.util.DiffUtil
import android.support.v7.widget.RecyclerView
import android.view.ViewGroup
import com.igc.list.paging.PageList

/**
 * @author baolongxiang
 * @createTime 2019-09-09
 */
interface IPagingAdapter {

    var itemData: PageList<*>

    fun onCreateViewHolder(parent: ViewGroup, indexViewType: Int): RecyclerView.ViewHolder

    fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int)

    fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int, payloads: List<Any>)

    fun getItemViewType(position: Int): Int

    fun registerAdapterDataObserver(observer: RecyclerView.AdapterDataObserver)

    fun unregisterAdapterDataObserver(observer: RecyclerView.AdapterDataObserver)

    fun onAttachedToRecyclerView(recyclerView: RecyclerView)

    fun onDetachedFromRecyclerView(recyclerView: RecyclerView)

    fun onViewAttachedToWindow(holder: RecyclerView.ViewHolder)

    fun onViewDetachedFromWindow(holder: RecyclerView.ViewHolder)

    fun notifyDataSetChanged()

    fun notifyItemInserted(position: Int)

    fun notifyItemChanged(position: Int)

    fun notifyItemRemoved(position: Int)

    fun notifyItemRangeChanged(positionStart: Int, itemCount: Int)

    fun getDiffCallback(): DiffUtil.Callback?
}