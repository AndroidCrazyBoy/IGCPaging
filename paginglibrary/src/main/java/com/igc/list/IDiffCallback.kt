package com.igc.list

/**
 * 使用方法参考DiffUtil
 *
 * @author baolongxiang
 * @createTime 2019-11-06
 */
interface IDiffCallback {

    fun areItemsTheSame(oldData: Any?, newData: Any?): Boolean

    fun areContentsTheSame(oldData: Any?, newData: Any?): Boolean

    fun getChangePayload(oldData: Any?, newData: Any?): Any?
}