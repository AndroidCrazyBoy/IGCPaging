package com.igc.list.paging

import android.support.annotation.Keep

/**
 * @author baolongxiang
 * @createTime 2019-07-07
 */
enum class Status {
    IDEAL,
    RUNNING,
    SUCCESS,
    FAILED
}

@Keep
@Suppress("DataClassPrivateConstructor")
data class NetworkState private constructor(
    val status: Status,
    val msg: String? = null) {
    companion object {
        val IDEAL = NetworkState(Status.IDEAL)
        val LOADING = NetworkState(Status.RUNNING)
        val LOADED = NetworkState(Status.SUCCESS)
        val COMPLETE = NetworkState(Status.SUCCESS)
        val COMPLETE_WITHOUT_TEXT = NetworkState(Status.SUCCESS)
        fun error(msg: String?) = NetworkState(Status.FAILED, msg)
    }
}