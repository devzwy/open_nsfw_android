package com.zww.sample.ui.databindingadapters

import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.chad.library.adapter.base.BaseQuickAdapter

@BindingAdapter(requireAll = false, value = arrayOf("bindAdapter"))
fun RecyclerView.bind(adapter: BaseQuickAdapter<*, *>) {
    this.adapter = adapter
    this.layoutManager = LinearLayoutManager(context)
}