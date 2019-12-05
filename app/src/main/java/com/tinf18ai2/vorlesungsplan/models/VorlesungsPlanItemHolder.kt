package com.tinf18ai2.vorlesungsplan.models

import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView


class VorlesungsPlanItemHolder(
    var titleTextView: TextView,
    var timeTextView: TextView,
    var descriptionTextView: TextView,
    var progessBar: ProgressBar,
    v: View
) :
    RecyclerView.ViewHolder(v)