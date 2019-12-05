package com.tinf18ai2.vorlesungsplan.backend_services.time_estimation

import android.annotation.SuppressLint
import com.tinf18ai2.vorlesungsplan.models.FABDataModel
import com.tinf18ai2.vorlesungsplan.models.VorlesungsplanItem
import com.tinf18ai2.vorlesungsplan.models.Vorlesungstag
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import java.util.logging.Logger

class TimeEstimator {

    var log: Logger = Logger.getGlobal()

    fun getFABData(week: List<Vorlesungstag>): FABDataModel? {

        val erg = timeToEndOfCurrentClass(week)
        if (erg == null) {
            val today = getToday(week)
            if (today != null) {
                log.info("Searching next class")
                return timeToNextClass(week)
            }
        }
        return erg
    }

    fun getCurrentClassOfToday(today: Vorlesungstag): VorlesungsplanItem? {
        val now = getMinutesOfToday()
        for (item in today.items) {
            val beg =
                getMinutesOfDay(item.startTime)//getMinutes(today.select("ul").first().children().last().select("div.cal-time").text().substring(0,5))

            val end =
                getMinutesOfDay(item.endTime)//getMinutes(today.select("ul").first().children().last().select("div.cal-time").text().substring(6,11))

            if (now in beg..end) {
                return item
            }
        }
        return null
    }

    fun getMinutesOfDay(time: Date?): Int {
        val date1: Date = time!!
        try {
            val hours = SimpleDateFormat("HH")
            val minutes = SimpleDateFormat("mm")
            return Integer.parseInt(hours.format(date1)) * 60 + Integer.parseInt(
                minutes.format(
                    date1
                )
            )
        } catch (e: ParseException) {
            e.printStackTrace()
        }
        return 0
    }

    fun getMinutesOfDay(time: String): Int {
        return getMinutesOfDay(SimpleDateFormat("HH:mm").parse(time))
    }

    fun getMinutesOfToday(): Int {
        val format = SimpleDateFormat("HH:mm")
        return getMinutesOfDay(format.format(Date(System.currentTimeMillis())))
        //return(getMinutes("11:00"))//For testing
    }

    private fun timeToNextClass(week: List<Vorlesungstag>): FABDataModel? {
        val todayDate: Date = SimpleDateFormat("dd.MM").parse(getTodayDateString())!!
        var days = 0
        for (day in week) {
            if (!day.tagDate.before(todayDate)) {
                for (item in day.items) {
                    if (item.progress == 0) {
                        return timeToClass(item, days)
                    }
                }
                days++
            }
        }

        return null
    }

    private fun timeToClass(item: VorlesungsplanItem, days: Int): FABDataModel? {
        val erg = timeToClass(item, false)
        if (erg != null) {
            while (erg.mins < 0) {
                erg.mins += 60
                erg.hours--
            }
            while (erg.hours < 0) {
                erg.hours += 24
                erg.days--
            }
            if (days < 0) {
                log.info("ERROR: Days <0 !!!! in TimeUntilUniEnd timeTo function")
            }
            erg.to = false
        }
        return erg
    }

    private fun timeToEndOfCurrentClass(today: Vorlesungstag): FABDataModel? {
        val current = getCurrentClassOfToday(today)
        if (current != null) {
            return timeToClass(current, true)
        }
        return null
    }

    private fun timeToClass(item: VorlesungsplanItem, toEnd: Boolean): FABDataModel? {
        val erg =
            FABDataModel(
                -1,
                -1,
                -1,
                ""
            )
        val now = getMinutesOfToday()
        val allMins =
            if (toEnd) {
                getMinutesOfDay(item.endTime) - now
            } else {
                getMinutesOfDay(item.startTime) - now
            }
        if (toEnd && allMins < 0) {
            return null
        }
        erg.mins = allMins % 60
        erg.hours = (allMins - erg.mins) / 60
        erg.days = 0
        erg.name = item.title
        log.info("Time to: " + erg)
        return erg
    }

    private fun timeToEndOfCurrentClass(week: List<Vorlesungstag>): FABDataModel? {
        val todayElem: Vorlesungstag? = getToday(week)
        try {
            return timeToEndOfCurrentClass(todayElem!!)
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return null
    }

    private fun getToday(week: List<Vorlesungstag>): Vorlesungstag? {
        val formatter = SimpleDateFormat("dd.MM")
        var today: Vorlesungstag? = null
        val todayDate: String = getTodayDateString()
        for (day in week) {
            if (formatter.format(day.tagDate) == todayDate) {
                today = day
                break
            }
        }
        return today
    }

    fun getTodayDateString(): String {
        val formatter = SimpleDateFormat("dd.MM")
        val date = Date(System.currentTimeMillis())
        val todayDate: String = formatter.format(date)
        return todayDate
    }
}

