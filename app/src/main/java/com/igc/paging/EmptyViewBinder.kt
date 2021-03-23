package com.igc.list

import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.igc.paging.EmptyBean
import me.drakeet.multitype.ItemViewBinder

/**
 * @author baolongxiang
 * @createTime 2019-08-18
 */
class EmptyViewBinder : ItemViewBinder<EmptyBean, EmptyViewBinder.ViewHolder>() {
    override fun onCreateViewHolder(inflater: LayoutInflater, parent: ViewGroup): ViewHolder {
        return ViewHolder(inflater.inflate(R.layout.empty_item, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, item: EmptyBean) {
    }


    inner class ViewHolder(val view: View) : androidx.recyclerview.widget.RecyclerView.ViewHolder(view) {
    }
}