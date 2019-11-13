package com.tinf18ai2.vorlesungsplan

import java.util.*

class VorlesungsplanItem(
    val title: String,
    val date: String, //Tag der Vorlesung
    val startTime: String, //Startzeit (Stunde + Minute)
    val endTime: String, //Startzeit (Stunde + Minute)
    val description: String //Raum...
) {
}