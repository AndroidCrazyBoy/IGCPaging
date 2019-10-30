package com.igc.list

import com.igc.list.paging.DataSource
import com.igc.list.paging.fetchPaing
import com.igc.paging.BaseBean

/**
 * @author baolongxiang
 * @createTime 2019-08-18
 */
class TestRepository : DataSource.DataFactory<String, BaseBean> {
    override fun createData(): DataSource<String, BaseBean> {
        return TestDataSource()
    }

    fun getTestData(param: String): Listing<BaseBean> {
        return fetchPaing(param)
    }
}