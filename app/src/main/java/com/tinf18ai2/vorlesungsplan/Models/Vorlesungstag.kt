package com.tinf18ai2.vorlesungsplan.Models

import com.tinf18ai2.vorlesungsplan.Models.VorlesungsplanItem
import java.util.*


class Vorlesungstag(
    val tag: String, val items: List<VorlesungsplanItem>, val tagDate:Date
)
