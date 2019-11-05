package com.igc.paging

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import com.igc.list.EmptyViewBinder
import com.igc.list.ListManager
import com.igc.list.R
import com.igc.list.paging.Status
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private val repository: TestRepository by lazy { TestRepository() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val listManager = ListManager.Builder()
                .setAdapter(buildAdapter())
                .setLayoutManager(LinearLayoutManager(this))
                .bindPageList(repository.getTestData("TEST PAGING"))
                .into(recyclerView, refreshLayout)
                .build(this)


        listManager.getRefreshState { state ->
            if (state?.status == Status.FAILED) {
                // fail
            }
        }

        test.setOnClickListener {
            listManager.changePageList {
                it?.removeAt2(0)
                it
//                return@changePageList it
            }
        }

//         listManager.bindPageList(repository.getTestData("TEST PAGING"))
//        refreshLayout.autoRefresh()
    }

    private fun buildAdapter(): IGCPagingAdapter {
        return IGCPagingAdapter().apply {
            register(TestBean::class.java, TestViewBinder())
            register(EmptyBean::class.java, EmptyViewBinder())
        }
    }
}