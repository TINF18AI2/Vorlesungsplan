package com.tinf18ai2.vorlesungsplan.models

import java.util.*


class Vorlesungstag(

    val tag: String,
    val items: List<VorlesungsplanItem>,
    val tagDate: Date

)
