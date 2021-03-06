package com.igc.list.paging

import androidx.annotation.Keep

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
        val IDEAL = NetworkState(Status.IDEAL, "ideal")
        val LOADING = NetworkState(Status.RUNNING, "loading")
        val LOADED = NetworkState(Status.SUCCESS, "loaded")
        val COMPLETE = NetworkState(Status.SUCCESS, "completed")
        val COMPLETE_WITHOUT_TEXT = NetworkState(Status.SUCCESS, "complete without holder text")
        fun error(msg: String?) = NetworkState(Status.FAILED, msg)
    }
}