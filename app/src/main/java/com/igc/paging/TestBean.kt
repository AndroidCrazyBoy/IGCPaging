package com.igc.paging

/**
 * @author baolongxiang
 * @createTime 2019-10-30
 */
data class TestBean(var text: String?, var res: Int = 0) : BaseBean() {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is TestBean) return false

        if (res != other.res) return false

        return true
    }

    override fun hashCode(): Int {
        return res
    }
}