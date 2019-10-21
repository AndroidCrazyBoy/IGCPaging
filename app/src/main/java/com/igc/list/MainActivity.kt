package com.igc.list

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.igc.list.paging.Status
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private val repository: TestRepository by lazy { TestRepository() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val listManager = ListManager.Builder()
            .setAdapter(buildAdapter())
            .bindPageList(repository.getTestData("TEST PAGING"))
            .into(recyclerView, refreshLayout)
            .build(this)


        listManager.getRefreshState { state ->
            if (state == Status.FAILED) {
                // fail
            }
        }

        // 也可以这么写、
        // listManager.bindPageList(repository.getTestData("TEST PAGING"))

//        refreshLayout.autoRefresh()

    }

    private fun buildAdapter(): IGCPagingAdapter {
        return IGCPagingAdapter().apply {
            register(String::class.java, TestViewBinder())
        }
    }
}