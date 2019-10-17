package com.igc.list.paging

import android.support.annotation.Keep
import kotlin.collections.AbstractList
import kotlin.collections.ArrayList

/**
 * 数据存储类
 * @author baolongxiang
 * @createTime 2019-07-05
 */
abstract class PageList<T>(val dataSource: PageKeyDataSource<*, T>) : AbstractList<T>() {

    /**
     * 每一页数据（总数据）
     */
    private val pageStore = mutableListOf<T>()

    /**
     * 以每一页数据为单位进行存储，数组个数为页数
     */
    private val pageSizeStore: MutableList<List<T>> = mutableListOf()

    /**
     * adapter notify callback
     */
    private val notifyCallbacks = mutableListOf<NotifyCallback>()

    companion object {
        fun <Key, Value> create(key: Key?, config: Config, dataSource: DataSource<Key, Value>): PageList<Value> {
            return ContiguousPageList(key, config, dataSource as PageKeyDataSource<Key, Value>)
        }
    }

    fun initData(data: List<T>) {
        val oldData = ArrayList<T>(pageStore)
        pageStore.clear()
        pageStore.addAll(data)
        pageSizeStore.clear()
        pageSizeStore.add(data)
        notifyChange(oldData, pageStore)
    }

    fun addData(data: List<T>) {
        if (data.isEmpty()) {
            return
        }
        val oldData = ArrayList<T>(pageStore)
        pageStore.addAll(data)
        pageSizeStore.add(data)
        notifyChange(oldData, pageStore)
    }

    private fun notifyChange(oldData: List<T>, newData: List<T>) {
        notifyCallbacks.forEach {
            it.onDataChange(oldData, newData)
        }
    }

    fun addNotifyCallback(callback: NotifyCallback) {
        if (!notifyCallbacks.contains(callback)) {
            notifyCallbacks.add(callback)
        }
    }

    override val size: Int
        get() = pageStore.size

    val pageCount: Int
        get() = pageSizeStore.size

    override fun get(index: Int): T {
        return pageStore.get(index)
    }

    fun add(t: T) {
        pageStore.add(t)
    }

    fun add(index: Int, t: T) {
        pageStore.add(index, t)
    }

    fun loadAround(index: Int) {
        if (index < 0 || index >= size) {
            throw IndexOutOfBoundsException("Paging Index: $index, Size: $size")
        }
        loadAroundInternal(index)
    }

    internal abstract fun loadAroundInternal(index: Int)

    interface NotifyCallback {
        fun <T> onDataChange(oldData: List<T>, newData: List<T>)
    }

    /**
     * pagin配置类
     * @param pageSize: 一页加载数
     * @param initPageSize: 首屏页加载数
     */
    @Keep
    data class Config(val pageSize: Int, val initPageSize: Int = pageSize) {
        companion object {
            val defaultConfig = Config(10)
        }
    }
}