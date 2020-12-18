package com.igc.list.paging

import android.support.annotation.Keep
import com.orhanobut.logger.Logger
import java.io.*
import java.util.*
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
    internal val pageStore = mutableListOf<T>()

    /**
     * adapter notify callback
     */
    internal val notifyCallbacks = mutableListOf<NotifyCallback>()

    /**
     * 存储[PageKeyDataSource.filterDuplicatesCondition]返回的数据，用于去重处理
     */
    internal val filterDuplicateIds = mutableListOf<Long>()

    companion object {
        const val FILTER_IGNORE = -1L

        fun <Key, Value> create(
            key: Key?,
            config: Config,
            dataSource: DataSource<Key, Value>
        ): PageList<Value> {
            return ContiguousPageList(key, config, dataSource as PageKeyDataSource<Key, Value>)
        }
    }

    fun initData(data: Collection<T>) {
        pageStore.clear()
        pageStore.addAll(data)
        filterDuplicateIds.clear()

        notifyChange(Collections.emptyList(), pageStore)
    }

    fun addData(data: Collection<T>) {
        if (data.isEmpty()) {
            return
        }
        val oldData = ArrayList<T>(pageStore)
        pageStore.addAll(data)
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

    override fun get(index: Int): T {
        return pageStore.get(index)
    }

    fun copyPageList(deepCopy: Boolean = true): PageList<T> {
        val copyResult = object : PageList<T>(dataSource) {
            override fun loadAroundInternal(index: Int) {
                this@PageList.loadAroundInternal(index)
            }
        }
        copyResult.pageStore.addAll(if (deepCopy) deepCopy(pageStore) else pageStore)
        copyResult.filterDuplicateIds.addAll(filterDuplicateIds)
        copyResult.notifyCallbacks.addAll(notifyCallbacks)
        return copyResult
    }

    /**
     * 深度copy
     */
    @Suppress("UNCHECKED_CAST")
    fun deepCopy(src: List<T>): List<T> {
        var byteOut: OutputStream? = null
        var outStream: ObjectOutputStream? = null
        var byteIn: InputStream? = null
        var inStream: ObjectInputStream? = null
        try {
            byteOut = ByteArrayOutputStream()
            outStream = ObjectOutputStream(byteOut)
            outStream.writeObject(src)
            byteIn = ByteArrayInputStream(byteOut.toByteArray())
            inStream = ObjectInputStream(byteIn)
            return inStream.readObject() as List<T>
        } catch (e: Exception) {
            return Collections.emptyList()
        } finally {
            byteOut?.close()
            outStream?.close()
            byteIn?.close()
            inStream?.close()
        }
    }

    fun removeAt2(index: Int): T? {
        if (pageStore.isEmpty()) {
            return null
        }
        removeFilterId(get(index))
        return pageStore.removeAt(index)
    }

    fun removeAll2(elements: Collection<T>): Boolean {
        return pageStore.removeAll(elements)
    }

    fun remove2(element: T): Boolean {
        removeFilterId(element)
        return pageStore.remove(element)
    }

    fun add2(element: T): Boolean {
        if (addFilterId(element)) {
            return pageStore.add(element)
        }
        return false
    }

    fun add2(index: Int, element: T) {
        if (addFilterId(element)) {
            pageStore.add(index, element)
        }
    }

    fun addAll2(elements: Collection<T>): Boolean {
        return pageStore.addAll(filterDuplicatesIfNeeded(elements))
    }

    fun addAll2(index: Int, elements: Collection<T>): Boolean {
        return pageStore.addAll(index, filterDuplicatesIfNeeded(elements))
    }

    fun clear2() {
        pageStore.clear()
        filterDuplicateIds.clear()
    }

    fun containsAll2(elements: PageList<T>?): Boolean {
        elements ?: return false
        return pageStore.containsAll(elements)
    }

    fun loadAround(index: Int) {
        if (index in 1 until size) {
            loadAroundInternal(index)
        }
    }

    fun filterDuplicatesIfNeeded(elements: Collection<T>): Collection<T> {
        return dataSource.filterDuplicatesCondition()?.let { callback ->
            val result = elements.toMutableList()
            try {
                val iterator = result.iterator()
                while (iterator.hasNext()) {
                    val id = callback(iterator.next())
                    if (id != FILTER_IGNORE && filterDuplicateIds.contains(id)) {
                        // 重复过滤
                        Logger.i("Paging --- > 过滤 ID: %d", id)
                        iterator.remove()
                    } else {
                        filterDuplicateIds.add(id)
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
            result
        } ?: elements
    }

    private fun addFilterId(element: T): Boolean {
        dataSource.filterDuplicatesCondition()?.invoke(element)?.let { id ->
            if (filterDuplicateIds.contains(id)) {
                return false
            }
            filterDuplicateIds.add(id)
        }
        return true
    }

    private fun removeFilterId(element: T) {
        dataSource.filterDuplicatesCondition()?.invoke(element)?.let { id ->
            filterDuplicateIds.remove(id)
        }
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