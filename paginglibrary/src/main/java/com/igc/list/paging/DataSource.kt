package com.igc.list.paging

import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable

/**
 * 获取和分发数据
 * @author baolongxiang
 * @createTime 2019-07-04
 */
abstract class DataSource<Key, Value> {

    /**
     * 兼容rxjava
     */
    private val disposableGroup = CompositeDisposable()

    fun Disposable.addDispose() {
        disposableGroup.add(this)
    }

    fun destroy() {
        disposableGroup.dispose()
    }

    /**
     * 分发请求初始化的数据（第一页数据）
     */
    abstract fun dispatchLoadInitial(key: Key?, pageSize: Int, receiver: PageResult.Receiver<Value>)

    /**
     * 分发请求下一页数据
     */
    abstract fun dispatchLoadAfter(key: Key?, pageSize: Int, receiver: PageResult.Receiver<Value>)

    interface DataFactory<Key, Value> {
        fun createData(): DataSource<Key, Value>
    }
}