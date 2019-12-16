package com.tinf18ai2.vorlesungsplan.models

/**
 * Holds information about the Current Vorlesung and the time left until end
 */
class FABDataModel(

    var timestamp: Long,

    var days: Int,
    var hours: Int,
    var mins: Int,
    var name: String,
    var to: Boolean = true

)