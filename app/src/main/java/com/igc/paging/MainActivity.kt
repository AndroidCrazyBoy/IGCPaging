package com.igc.paging

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.igc.list.*
import com.igc.list.paging.Status
import com.orhanobut.logger.Logger
import kotlinx.android.synthetic.main.activity_main.*


@Suppress("UNCHECKED_CAST")
class MainActivity : AppCompatActivity() {

    private val repository: TestRepository by lazy { TestRepository() }

    private var index = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val listManager = ListManager.Builder()
            .setAdapter(buildAdapter())
            .setLayoutManager(
                LinearLayoutManager(
                    this
                )
            )
            .enableNotifyAnim(true)
            .bindPageList(repository.getTestData("TEST PAGING"))
            .into(recyclerView, refreshLayout)
            .build(this)


        listManager.observeRefreshState { state ->
            if (state?.status == Status.SUCCESS) {
                Logger.d("TEST --- SUCCESS")
            }
            Logger.d("TEST --- state=${state?.msg}")
        }

        testItemDelete.setOnClickListener {
            listManager.changePageList(true) {
                it?.removeAt2(0)
                it
            }
        }

        testItemNotify.setOnClickListener {
//            listManager.changePageList {
//                it ?: return@changePageList it
//                (it[0] as TestBean).otherText = "00000000"
//                (it[1] as TestBean).otherText = "11111111"
//                it.forEach {
//                    (it as TestBean).otherText = "123123123"
//                }
//                it
//            }

            listManager.bindPageList(repository.getTestData("REBIND PAGING ${index++}") as Listing<Any>)
        }

//         listManager.bindPageList(repository.getTestData("TEST PAGING"))
//        refreshLayout.autoRefresh()
    }

    private fun buildAdapter(): IGCPagingAdapter {
        return IGCPagingAdapter(callback).apply {
            register(TestBean::class.java, TestViewBinder())
            register(EmptyBean::class.java, EmptyViewBinder())
        }
    }

    private val callback = object : IDiffCallback {
        override fun areItemsTheSame(oldData: Any?, newData: Any?): Boolean {
            if (oldData !is TestBean || newData !is TestBean) {
                return false
            }
            return oldData.text == newData.text
        }

        override fun areContentsTheSame(oldData: Any?, newData: Any?): Boolean {
            if (oldData !is TestBean || newData !is TestBean) {
                return false
            }
            return oldData.otherText == newData.otherText
        }

        override fun getChangePayload(oldData: Any?, newData: Any?): Any? {
            if (oldData !is TestBean || newData !is TestBean) {
                return null
            }
            val bundle = Bundle()
            bundle.putString("TEST", newData.otherText)
            return bundle
        }
    }
}