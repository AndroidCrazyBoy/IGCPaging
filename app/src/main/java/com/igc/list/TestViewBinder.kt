package com.igc.list

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.test_item.view.*
import me.drakeet.multitype.ItemViewBinder

/**
 * @author baolongxiang
 * @createTime 2019-08-18
 */
class TestViewBinder : ItemViewBinder<String, TestViewBinder.ViewHolder>() {
    override fun onCreateViewHolder(inflater: LayoutInflater, parent: ViewGroup): ViewHolder {
        return ViewHolder(inflater.inflate(R.layout.test_item, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, item: String) {
        holder.bind(item)
    }

    inner class ViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
        fun bind(item: String) {
            view.itemText.setText(item)
        }
    }
}