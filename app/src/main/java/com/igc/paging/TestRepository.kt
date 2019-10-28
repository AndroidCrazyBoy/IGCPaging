package com.igc.list

import com.igc.list.paging.DataSource
import com.igc.list.paging.fetchPaing
import com.igc.paging.TestDataSource

/**
 * @author baolongxiang
 * @createTime 2019-08-18
 */
class TestRepository : DataSource.DataFactory<String, String> {
    override fun createData(): DataSource<String, String> {
        return TestDataSource()
    }

    fun getTestData(param: String): Listing<String> {
        return fetchPaing(param)
    }
}