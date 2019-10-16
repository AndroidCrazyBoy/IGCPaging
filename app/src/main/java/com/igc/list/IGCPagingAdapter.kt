package com.igc.list

import android.support.v7.util.DiffUtil
import com.igc.list.paging.PageList
import me.drakeet.multitype.MultiTypeAdapter

/**
 * @author baolongxiang
 * @createTime 2019-09-09
 */
class IGCPagingAdapter(val callback: DiffUtil.Callback? = null) : MultiTypeAdapter(), IPagingAdapter {
    override var itemData: PageList<*>
        get() = items as PageList<*>
        set(value) {
            items = value
        }

    override fun getDiffCallback(): DiffUtil.Callback? {
        return callback
    }
}