package com.tinf18ai2.vorlesungsplan.ui

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.opengl.Visibility
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.tinf18ai2.vorlesungsplan.models.VorlesungsplanItem
import com.tinf18ai2.vorlesungsplan.R
import com.tinf18ai2.vorlesungsplan.ui.MainActivity.Companion.LOG
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
        val descriptionTextView: TextView = view.textViewDescription
        val progessBar: ProgressBar = view.progressBar

        return VorlesungsPlanItemHolder(
            titleTextView = title,
            timeTextView = time,
            descriptionTextView = descriptionTextView,
            progessBar = progessBar,
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
        LOG.info("set layout: \ntitleTextView: ${holder.titleTextView.text}\ntimeTextView:${holder.timeTextView.text}")
        if (item.isDay) {
            holder.titleTextView.setTextSize(20.toFloat())
            holder.titleTextView.setTextColor(Color.parseColor("#820000"))
            holder.progessBar.visibility = View.INVISIBLE

        } else {
            holder.titleTextView.setTextSize(16.toFloat())
            holder.titleTextView.setTextColor(Color.GRAY)

            //layout progress bar
            val progress = item.progress
            holder.progessBar.visibility = View.VISIBLE
            holder.progessBar.progress = progress
            holder.progessBar.rotation = 90.toFloat()

            //color progress bar

            if (progress <= 20) {
                holder.progessBar.progressTintList = ColorStateList.valueOf(Color.RED)
            } else if (progress >= 80) {
                holder.progessBar.progressTintList = ColorStateList.valueOf(Color.GREEN)
            }else{
                holder.progessBar.progressTintList = ColorStateList.valueOf(Color.parseColor("#dd8f1d"))
            }
        }
    }
}

class VorlesungsPlanItemHolder(
    var titleTextView: TextView,
    var timeTextView: TextView,
    var descriptionTextView: TextView,
    var progessBar: ProgressBar,
    v: View
) :
    RecyclerView.ViewHolder(v)


