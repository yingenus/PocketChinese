package com.yingenus.pocketchinese.view.holders

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.recyclerview.widget.RecyclerView

open class ViewViewHolder : RecyclerView.ViewHolder {
    constructor(v: View):super(v)
    constructor(@LayoutRes id:Int, inflater: LayoutInflater):this(inflater.inflate(id,null))
    constructor(@LayoutRes id:Int, inflater: LayoutInflater, root : ViewGroup) : this(inflater.inflate(id,root,false))
}