package com.tinf18ai2.vorlesungsplan.models

class FABDataModel(
    var days: Int,
    var hours: Int,
    var mins: Int,
    var name: String,
    var to: Boolean = true
) //Holds information about the Current Vorlesung and the time left until end