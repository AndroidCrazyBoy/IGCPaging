package com.igc.paging

import com.igc.list.IDiffCallback
import com.igc.list.IPagingAdapter
import com.igc.list.paging.PageList
import me.drakeet.multitype.MultiTypeAdapter

/**
 * @author baolongxiang
 * @createTime 2019-09-09
 */
class IGCPagingAdapter(val callback: IDiffCallback? = null) : MultiTypeAdapter(), IPagingAdapter {
    override var itemData: PageList<*>
        get() : PageList<*> {
            if (items is PageList<*>) {
                return items as PageList<*>
            } else {
                throw IllegalArgumentException("IGCPagingAdapter data must be use PageList")
            }
        }
        set(value) {
            items = value
        }

    override fun getDiffCallback(): IDiffCallback? {
        return callback
    }
}