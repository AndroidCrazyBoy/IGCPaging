package com.igc.paging

import android.os.Handler
import com.igc.list.paging.PageKeyDataSource

/**
 * @author baolongxiang
 * @createTime 2019-08-18
 */
class TestDataSource : PageKeyDataSource<String, BaseBean>() {

    private val handler: Handler = Handler()

    override fun loadInitial(params: LoadParams, callback: LoadCallback<BaseBean>) {
        itemIndex = 0
        handler.postDelayed({
            callback.onResult(createTestData(params.key, params.pageSize))
//            val result = mutableListOf<BaseBean>()
//            result.add(EmptyBean())
//            callback.onResult(result.toMutableList())
//            callback.onFinishWithoutNoMoreData()
        }, 500)
    }

    override fun loadAfter(params: LoadParams, callback: LoadCallback<BaseBean>) {
        handler.postDelayed({
            callback.onResult(createTestData(params.key, params.pageSize))
            if (params.pageIndex > 3) {
                callback.onFinish()
            }
        }, 300)
    }

    private var itemIndex = 0

    private fun createTestData(key: String?, pageSize: Int): MutableList<BaseBean> {
        val result = mutableListOf<BaseBean>()
        for (i in 0 until pageSize) {
            itemIndex++
            result.add(TestBean("$key + $itemIndex"))
        }
        return result
    }
}