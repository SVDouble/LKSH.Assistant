package com.lksh.dev.lkshassistant.views

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.lksh.dev.lkshassistant.R
import com.lksh.dev.lkshassistant.sqlite_helper.UserData

class UserCardAdapter(private val mContext: Context, private val dataset: ArrayList<UserData>) :
        RecyclerView.Adapter<UserCardAdapter.ViewHolder>() {

    class ViewHolder(v: View) : RecyclerView.ViewHolder(v) {
        val number = v.findViewById<TextView>(R.id.number)!!
        val name = v.findViewById<TextView>(R.id.name)!!
        val parallel = v.findViewById<TextView>(R.id.parallel)!!
        val home = v.findViewById<TextView>(R.id.home)!!
    }

    override fun onCreateViewHolder(parent: ViewGroup,
                                    viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.part_rv_building, parent, false)
        // set the view's size, margins, paddings and layout parameters
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val data = dataset[position]
        holder.number.text = position.toString()
        holder.name.text = data.name
        holder.parallel.text = data.parallel
        holder.home.text = data.house
    }

    override fun getItemCount() = dataset.size
}
