package com.igc.list.paging

import android.support.annotation.IntDef
import kotlin.annotation.Retention

/**
 * 获取结果类
 * @author baolongxiang
 * @createTime 2019-07-05
 */
class PageResult<T>(var page: List<T>) {

    @Retention(AnnotationRetention.SOURCE)
    @IntDef(INIT, APPEND, FINISHED)
    internal annotation class ResultType

    companion object {
        const val INIT = 0

        const val APPEND = 1

        const val FINISHED = 2
    }

    interface Receiver<T> {
        fun onPageResult(@ResultType type: Int, pageResult: PageResult<T>)
    }
}