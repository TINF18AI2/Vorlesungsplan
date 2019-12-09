package com.tinf18ai2.vorlesungsplan.ui

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.tinf18ai2.vorlesungsplan.R
import com.tinf18ai2.vorlesungsplan.models.Vorlesungstag
import com.tinf18ai2.vorlesungsplan.ui.MainActivity.Companion.LOG
import kotlinx.android.synthetic.main.recycler_view_day_layout.view.*

/**
 * This is the ViewHolder for all Vorlesungsplan Days
 */
class VorlesungsplanDayViewHolder(
    var view: View,
    var context: Context
) :
    RecyclerView.ViewHolder(view) {

    private lateinit var adapter: VorlesungsplanDayAdapter

    fun create() {
        view.recyclerViewDay.layoutManager = LinearLayoutManager(context)
    }

    fun bind(item: Vorlesungstag) {
        LOG.info(
            "Binding to VorlesungsplanDay:\n" +
                    "tag: ${item.tag}\n")

        // adapter
        adapter = VorlesungsplanDayAdapter(
            items = item.items,
            context = context
        )
        itemView.recyclerViewDay.adapter = adapter

        // data
        itemView.textViewDay.text = item.tag
    }

}

class VorlesungsplanWeekAdapter(
    val items: List<Vorlesungstag>,
    val context: Context
) :
    RecyclerView.Adapter<VorlesungsplanDayViewHolder>() {

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VorlesungsplanDayViewHolder {
        val view = LayoutInflater.from(context)
            .inflate(R.layout.recycler_view_day_layout, parent, false)
        val viewHolder = VorlesungsplanDayViewHolder(view, context)
        viewHolder.create()
        return viewHolder
    }

    override fun onBindViewHolder(holder: VorlesungsplanDayViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemViewType(position: Int): Int {
        return 0
    }

}
