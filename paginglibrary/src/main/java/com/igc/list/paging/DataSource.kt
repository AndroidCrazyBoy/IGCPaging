package com.igc.list.paging

/**
 * 获取和分发数据
 * @author baolongxiang
 * @createTime 2019-07-04
 */
abstract class DataSource<Key, Value> {

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