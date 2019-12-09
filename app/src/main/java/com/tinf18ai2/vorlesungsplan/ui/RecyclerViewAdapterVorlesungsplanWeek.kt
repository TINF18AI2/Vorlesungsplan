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
        LOG.info(
            "Binding to VorlesungsplanDay:\n" +
                    "tag: ${item.tag}\n")

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
    val items: List<Vorlesungstag>,
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