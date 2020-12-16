package com.igc.list.paging

import com.orhanobut.logger.Logger

/**
 * 请求数据的存储和分发
 * @author baolongxiang
 * @createTime 2019-07-05
 */
class ContiguousPageList<Key, Value>(
    private val key: Key?,
    private val config: Config,
    private val pageKeyDataSource: PageKeyDataSource<Key, Value>
) : PageList<Value>(pageKeyDataSource) {
    companion object {
        private const val PRE_LOAD_COUNT = 1
    }

    private val receiver: PageResult.Receiver<Value> = object : PageResult.Receiver<Value> {
        override fun onPageResult(type: Int, pageResult: PageResult<Value>) {
            when (type) {
                PageResult.INIT -> {
                    initData(filterDuplicatesIfNeeded(pageResult.page))
                }
                PageResult.APPEND -> {
                    addData(filterDuplicatesIfNeeded(pageResult.page))
                }
            }
        }
    }

    init {
        if (dataSource.isInitial()) {
            pageKeyDataSource.dispatchLoadInitial(key, config.initPageSize, receiver)
        } else {
            pageKeyDataSource.dispatchLoadAfter(key, config.pageSize, receiver)
        }
    }

    override fun loadAroundInternal(index: Int) {
        // 动态计算预加载距离
        val distance = if (size < Math.min(
                config.pageSize,
                config.initPageSize
            )
        ) config.pageSize - PRE_LOAD_COUNT else size - PRE_LOAD_COUNT
        Logger.d("Paging --- >loadAroundInternal index =" + index + ", distance=" + distance + ", canLoadMore=" + dataSource.canLoadMore() + ", LoadMore state=" + dataSource.loadMoreState.value)
        if (index >= distance && dataSource.canLoadMore()) {
            pageKeyDataSource.dispatchLoadAfter(key, config.pageSize, receiver)
        }
    }
}