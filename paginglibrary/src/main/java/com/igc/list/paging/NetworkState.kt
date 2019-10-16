package com.igc.list.paging

import android.support.annotation.Keep

/**
 * @author baolongxiang
 * @createTime 2019-07-07
 */
enum class Status {
    RUNNING,
    SUCCESS,
    FAILED,
    IDEAL,
    COMPLETE
}

@Keep
@Suppress("DataClassPrivateConstructor")
data class NetworkState private constructor(
    val status: Status,
    val msg: String? = null) {
    companion object {
        val LOADED = NetworkState(Status.SUCCESS)
        val LOADING = NetworkState(Status.RUNNING)
        val COMPLETE = NetworkState(Status.COMPLETE)
        val IDEAL = NetworkState(Status.IDEAL)
        fun error(msg: String?) = NetworkState(Status.FAILED, msg)
    }
}