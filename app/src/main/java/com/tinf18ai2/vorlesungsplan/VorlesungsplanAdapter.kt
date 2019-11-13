package com.tinf18ai2.vorlesungsplan

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.main_recycler_view_item_layout.view.*

class VorlesungsplanAdapter(val items: List<VorlesungsplanItem>, val context: Context) :
    RecyclerView.Adapter<VorlesungsPlanItemHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VorlesungsPlanItemHolder {

        val view = LayoutInflater.from(context).inflate(
            R.layout.main_recycler_view_item_layout,
            parent,
            false
        )
        val title: TextView = view.textViewTitle
        val time: TextView = view.textViewTime

        return VorlesungsPlanItemHolder(
            titleTextView = title, timeTextView = time, v = view

        )
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: VorlesungsPlanItemHolder, position: Int) {
        holder.titleTextView.text = items.get(position).title
        holder.timeTextView.text = items.get(position).time
    }
}

class VorlesungsPlanItemHolder(var titleTextView: TextView, var timeTextView: TextView, v: View) :
    RecyclerView.ViewHolder(v)


