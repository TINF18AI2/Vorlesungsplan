package com.tinf18ai2.vorlesungsplan

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
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
        val item = items.get(position)
        holder.titleTextView.text = item.title
        holder.timeTextView.text = item.time
        if (item.time.equals("")) {
            holder.titleTextView.setTextSize(25.toFloat())
            holder.titleTextView.setTextColor(Color.GRAY)
        }
    }
}

class VorlesungsPlanItemHolder(
    var titleTextView: TextView,
    var timeTextView: TextView,
    v: View
) :
    RecyclerView.ViewHolder(v)


