package com.tinf18ai2.vorlesungsplan.ui

import android.content.Context
import android.graphics.Rect
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.tinf18ai2.vorlesungsplan.R
import com.tinf18ai2.vorlesungsplan.models.Vorlesungstag
import com.tinf18ai2.vorlesungsplan.ui.MainActivity.Companion.LOG
import kotlinx.android.synthetic.main.recycler_view_day_layout.view.*
import java.util.*
import kotlin.math.max
import kotlin.math.min


/**
 * This is the ViewHolder for all Vorlesungsplan Days
 */
class ViewHolderVorlesungsplanDay(
    var view: View,
    var context: Context
) :
    RecyclerView.ViewHolder(view) {

    private lateinit var adapter: RecyclerViewAdapterVorlesungsplanDay

    fun create() {
        view.recyclerViewDay.layoutManager = LinearLayoutManager(context)
    }

    fun bind(item: Vorlesungstag) {
        // adapter
        adapter = RecyclerViewAdapterVorlesungsplanDay(
            items = item.items,
            context = context
        )
        itemView.recyclerViewDay.adapter = adapter

        // data
        itemView.textViewDay.text = item.tag
    }
}

class RecyclerViewAdapterVorlesungsplanWeek(
    var items: List<Vorlesungstag>,
    val context: Context
) :
    RecyclerView.Adapter<ViewHolderVorlesungsplanDay>() {

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolderVorlesungsplanDay {
        val view = LayoutInflater.from(context)
            .inflate(R.layout.recycler_view_day_layout, parent, false)
        val viewHolder = ViewHolderVorlesungsplanDay(view, context)
        viewHolder.create()
        return viewHolder
    }

    override fun onBindViewHolder(holder: ViewHolderVorlesungsplanDay, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemViewType(position: Int): Int {
        return 0
    }

    /**
     * Converts the day of week to the index of the respective element.
     *
     *  @param dayOfWeek day of the week, Sunday=1, Monday=2, Thuesday=3, ...
     *  @return index of the element or -1 if there are no elements
     *
     *  @see java.util.Calendar
     */
    fun convertDayOfWeekToIndex(dayOfWeek: Int): Int {
        // Monday should be the first day of the week, thus remove the offset
        var position = dayOfWeek - Calendar.MONDAY
        // If the index is now negative, add the length of the week = 7
        if (position < 0)
            position += 7
        // If there are not enough elements, return the index of the last element
        position = min(position, this.itemCount - 1)

        return position
    }
}

class ItemDecorationVorlesungsplanWeek(
    private val verticalSpaceHeight: Int
) :
    RecyclerView.ItemDecoration() {

    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    )  {
        if (parent.getChildAdapterPosition(view) != (parent.adapter!!.itemCount - 1)) {
            outRect.bottom = verticalSpaceHeight
        }
    }

}