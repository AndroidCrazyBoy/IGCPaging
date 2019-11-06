package com.igc.paging

import android.os.Bundle
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.igc.list.R
import kotlinx.android.synthetic.main.test_item.view.*
import me.drakeet.multitype.ItemViewBinder

/**
 * @author baolongxiang
 * @createTime 2019-08-18
 */
class TestViewBinder : ItemViewBinder<TestBean, TestViewBinder.ViewHolder>() {
    override fun onCreateViewHolder(inflater: LayoutInflater, parent: ViewGroup): ViewHolder {
        return ViewHolder(inflater.inflate(R.layout.test_item, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, item: TestBean) {
        holder.bind(item)
    }

    override fun onBindViewHolder(holder: ViewHolder, item: TestBean, payloads: MutableList<Any>) {
        if (payloads.isEmpty()) {
            onBindViewHolder(holder, item)
        } else {
            holder.view.itemText.text = (payloads[getPosition(holder)] as Bundle).getString("TEST")
        }
    }

    inner class ViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
        fun bind(item: TestBean) {
            view.itemText.text = item.text
        }
    }
}