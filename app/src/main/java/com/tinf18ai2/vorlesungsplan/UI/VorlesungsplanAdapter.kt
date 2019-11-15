package com.tinf18ai2.vorlesungsplan.UI

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.tinf18ai2.vorlesungsplan.Models.VorlesungsplanItem
import com.tinf18ai2.vorlesungsplan.R
import kotlinx.android.synthetic.main.main_recycler_view_item_layout.view.*
import org.w3c.dom.Text

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
        val descriptionTextView: TextView = view.textViewDescription

        return VorlesungsPlanItemHolder(
            titleTextView = title,
            timeTextView = time,
            descriptionTextView = descriptionTextView,
            v = view

        )
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: VorlesungsPlanItemHolder, position: Int) {
        val item = items.get(position)
        holder.titleTextView.text = item.title
        holder.timeTextView.text = item.time
        holder.descriptionTextView.text = item.description
        if (item.time.equals("")) {
            holder.titleTextView.setTextSize(20.toFloat())
            holder.titleTextView.setTextColor(Color.parseColor("#820000"))
//            holder.titleTextView.setTextColor(Color.GRAY)
        }
    }
}

class VorlesungsPlanItemHolder(
    var titleTextView: TextView,
    var timeTextView: TextView,
    var descriptionTextView: TextView,
    v: View
) :
    RecyclerView.ViewHolder(v)


