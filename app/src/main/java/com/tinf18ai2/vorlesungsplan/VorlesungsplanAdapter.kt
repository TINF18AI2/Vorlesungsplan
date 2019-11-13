package com.tinf18ai2.vorlesungsplan

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

class VorlesungsplanAdapter(val items: List<VorlesungsplanItem>, val context: Context) :
    RecyclerView.Adapter<VorlesungsPlanItemHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VorlesungsPlanItemHolder {
        return VorlesungsPlanItemHolder(
            LayoutInflater.from(context).inflate(
                R.layout.main_recycler_view_item_layout,
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: VorlesungsPlanItemHolder, position: Int) {
        holder?.textViewTitle = items.get(position).title
    }
}

class VorlesungsPlanItemHolder(v: View) : RecyclerView.ViewHolder(v){

    var textViewTitle : String = ""

}