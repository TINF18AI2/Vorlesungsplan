package com.tinf18ai2.vorlesungsplan.ui

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.tinf18ai2.vorlesungsplan.R
import com.tinf18ai2.vorlesungsplan.models.VorlesungsplanItem
import com.tinf18ai2.vorlesungsplan.ui.MainActivity.Companion.LOG
import kotlinx.android.synthetic.main.recycler_view_day_layout.view.*
import kotlinx.android.synthetic.main.recycler_view_item_layout.view.*
import kotlinx.android.synthetic.main.recycler_view_item_layout.view.textViewTitle
import java.util.*

/**
 * This is the BaseViewHolder all ViewHolder have to be a subclass of.
 */
abstract class BaseViewHolder<T>(itemView: View, val context: Context) : RecyclerView.ViewHolder(itemView) {

    abstract fun bind(item: T)

}

/**
 * This is the ViewHolder for all Vorlesungsplan Days
 */
class VorlesungsplanDayViewHolder(itemView: View, context: Context) :
    BaseViewHolder<VorlesungsplanItem>(itemView, context) {

    override fun bind(item: VorlesungsplanItem) {
        LOG.info(
            "Binding to VorlesungsplanDay:\n" +
                    "title: ${item.title}\n" +
                    "time: ${item.time}")
        // data
        itemView.textViewDay.text = item.title
        // itemView.textViewDate.text = item.time

        // day style
        itemView.textViewDay.textSize = 20.toFloat()
        itemView.textViewDay.setTextColor(context.getColor(R.color.date_title_color))
    }

}

/**
 * This is the ViewHolder for all Vorlesungsplan Items
 */
class VorlesungsplanItemViewHolder(itemView: View, context: Context) :
    BaseViewHolder<VorlesungsplanItem>(itemView, context) {

    override fun bind(item: VorlesungsplanItem) {
        LOG.info(
            "Binding to VorlesungsplanItem:\n" +
                    "title: ${item.title}\n" +
                    "time: ${item.time}")

        // data
        itemView.textViewTitle.text = item.title
        itemView.textViewTime.text = item.time
        itemView.textViewRoom.text = item.description

        // title style
        itemView.textViewTitle.textSize = 16.toFloat()
        itemView.textViewTitle.setTextColor(Color.GRAY)

        // progressBar style
        val progress = item.progress
        itemView.progressBar.visibility = View.VISIBLE
        itemView.progressBar.progress = progress
        itemView.progressBar.rotation = 90.toFloat()

        // progressBar style color
        when {
            progress <= 20 -> itemView.progressBar.progressTintList =
                ColorStateList.valueOf(context.getColor(R.color.progress_long))
            progress >= 80 -> itemView.progressBar.progressTintList =
                ColorStateList.valueOf(context.getColor(R.color.progress_short))
            progress == 100 -> itemView.progressBar.progressTintList =
                ColorStateList.valueOf(context.getColor(R.color.progress_done))
            else -> itemView.progressBar.progressTintList =
                ColorStateList.valueOf(context.getColor(R.color.progress_mid))
        }
    }

}

class VorlesungsplanDataAdapter( val items: List<VorlesungsplanItem>, val context: Context) :
    RecyclerView.Adapter<BaseViewHolder<*>>() {

    companion object {
        private const val TYPE_DAY = 0
        private const val TYPE_ITEM = 1
    }

    override fun getItemCount(): Int {
        return items.size
    }

    //--------onCreateViewHolder: inflate layout with view holder-------
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder<*> {
        return when (viewType) {
            TYPE_DAY -> {
                val view = LayoutInflater.from(context)
                    .inflate(R.layout.recycler_view_day_layout, parent, false)
                VorlesungsplanDayViewHolder(view, context)
            }
            TYPE_ITEM -> {
                val view = LayoutInflater.from(context)
                    .inflate(R.layout.recycler_view_item_layout, parent, false)
                VorlesungsplanItemViewHolder(view, context)
            }
            else -> throw IllegalArgumentException("Invalid view type")
        }
    }

    //-----------onCreateViewHolder: bind view with data model---------
    override fun onBindViewHolder(holder: BaseViewHolder<*>, position: Int) {
        when (holder) {
            is VorlesungsplanDayViewHolder -> holder.bind(items[position])
            is VorlesungsplanItemViewHolder -> holder.bind(items[position])
            else -> throw IllegalArgumentException()
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when (items[position].isDay) {
            true -> TYPE_DAY
            false -> TYPE_ITEM
        }
    }
}
