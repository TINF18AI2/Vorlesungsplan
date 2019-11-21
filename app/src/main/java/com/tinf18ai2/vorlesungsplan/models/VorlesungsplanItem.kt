package com.tinf18ai2.vorlesungsplan.models

import java.util.*

class VorlesungsplanItem(
    val title: String,
    val time: String, //Zeitstring (ANFANGSZEIT-ENDZEIT)
    val description: String, //Raum...
    val startTime : Date,   //Startzeit
    val endTime : Date,  //Endzeit
    val isDay: Boolean
)