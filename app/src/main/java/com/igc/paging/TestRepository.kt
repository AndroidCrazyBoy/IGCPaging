package com.igc.paging

import com.igc.list.Listing
import com.igc.list.paging.DataSource
import com.igc.list.paging.PageList
import com.igc.list.paging.fetchPaing

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