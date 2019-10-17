package com.igc.list.paging

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.Transformations
import com.igc.list.GlobalListInitializer
import com.igc.list.Listing


/**
 * 将datasource转换成PageList
 */
private fun <Key, Value> DataSource.DataFactory<Key, Value>.toLiveData(
    key: Key?,
    config: PageList.Config
): MutableLiveData<PageList<Value>> {
    val liveData = MutableLiveData<PageList<Value>>()
    liveData.value = PageList.create(key, config, createData())
    return liveData
}

/**
 * pageList组装成listing
 */
fun <Key, Value> DataSource.DataFactory<Key, Value>.fetchPaing(
    key: Key? = null,
    config: PageList.Config = GlobalListInitializer.instance.getPagingConfig()
): Listing<Value> {
    val liveData = toLiveData(key, config)
    return Listing(
        pagedList = liveData,
        loadMoreState = liveData.value?.dataSource?.loadMoreState,
        refreshState = liveData.value?.dataSource?.initialLoad,
        refresh = { liveData.value?.dataSource?.refresh() ?: {} },
        retry = { liveData.value?.dataSource?.retry() ?: {} }
    )
}