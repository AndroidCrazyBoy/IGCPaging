package com.igc.list

import android.os.Handler
import com.igc.list.paging.PageKeyDataSource

/**
 * @author baolongxiang
 * @createTime 2019-08-18
 */
class TestDataSource : PageKeyDataSource<String, String>() {

    private val handler: Handler = Handler()

    override fun loadInitial(params: LoadParams, callback: LoadCallback<String>) {
        itemIndex = 0
        handler.postDelayed({
            callback.onResult(createTestData(params.key, params.pageSize))
        }, 500)
    }

    override fun loadAfter(params: LoadParams, callback: LoadCallback<String>) {
        handler.postDelayed({
            callback.onResult(createTestData(params.key, params.pageSize))
            if (params.pageIndex > 5) {
                callback.onFinish()
            }
        }, 300)
    }

    private var itemIndex = 0

    private fun createTestData(key: String?, pageSize: Int): MutableList<String> {
        val result = mutableListOf<String>()
        for (i in 0 until pageSize) {
            itemIndex++
            result.add("$key + $itemIndex")
        }
        return result
    }
}