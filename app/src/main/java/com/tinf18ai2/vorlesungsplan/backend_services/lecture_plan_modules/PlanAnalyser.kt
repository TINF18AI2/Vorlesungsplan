package com.tinf18ai2.vorlesungsplan.backend_services.lecture_plan_modules

import com.tinf18ai2.vorlesungsplan.backend_services.time_estimation.TimeEstimator
import com.tinf18ai2.vorlesungsplan.models.VorlesungsplanItem
import com.tinf18ai2.vorlesungsplan.models.Vorlesungstag
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import java.text.SimpleDateFormat
import java.util.*
import java.util.logging.Logger
import kotlin.collections.ArrayList


class PlanAnalyser {

    val URL =
        "https://vorlesungsplan.dhbw-mannheim.de/index.php?action=view&gid=3067001&uid=7431001"
    val ICAL_URL = "http://vorlesungsplan.dhbw-mannheim.de/ical.php?uid=7431001"

    var log: Logger = Logger.getGlobal()

    fun analyse(): List<Vorlesungstag>? {
        return getData(0)
    }

    fun analyse(shiftWeeks: Int): List<Vorlesungstag>? {
        return getData(shiftWeeks)
    }

    private fun getIcalData(): List<Vorlesungstag>? {
        val week: ArrayList<Vorlesungstag> = ArrayList()

        return week
    }

    private fun calculateTimeStamp(shiftWeeks: Int): String {
        var shiftDays = 7 * shiftWeeks
        val base = "&date="
        val dayMillis = 24 * 60 * 60
        var time = Date().time / 1000 + (1 + shiftDays) * dayMillis
        return base + time
    }

    private fun getData(shiftWeeks: Int): List<Vorlesungstag>? {    //reads out the Information from the Website and saves it in the returned Array
        val week: ArrayList<Vorlesungstag> = ArrayList()    //Holds Information about the hole week
        val site: Document = readWebsite(URL + calculateTimeStamp(shiftWeeks))
            ?: return null  //returns null if site==null

        val days = site.getElementsByClass("ui-grid-e").first()
            .children()//Array which holds information about every day in the week

        for (day in days) {
            val items = ArrayList<VorlesungsplanItem>() //Every Vorlesung of the day
            var first = true
            if (day.select("ul").isNotEmpty()) {
                if (day.select("ul").first().children().isNotEmpty()) {
                    for (elem in day.select("ul").first().children()) {

                        if (first) {
                            first = false
                        } else {
                            val timeString = elem.getElementsByClass("cal-time").first().text()
                            val times = getTimes(timeString)
                            var info: String =
                                if (elem.getElementsByClass("cal-res").isEmpty()) {
                                    elem.getElementsByClass("cal-text").first().text()
                                } else {
                                    elem.getElementsByClass("cal-res").first().text()
                                }
                            items.add(
                                VorlesungsplanItem(
                                    elem.getElementsByClass("cal-title").first().text(),
                                    timeString,
                                    info,
                                    times.start,
                                    times.end,
                                    0
                                )
                            )
                        }

                    }
                }
            }

            if (day.getElementsByAttributeValue("data-role", "list-divider").isNotEmpty()) {
                val dayString: String =
                    day.getElementsByAttributeValue("data-role", "list-divider").first().text()
                var tag =
                    Vorlesungstag(
                        dayString,
                        items,
                        isolateTime(dayString)
                    )
                week.add(setProgresses(tag))
            }
        }
        return week

    }

    private fun setProgresses(day: Vorlesungstag): Vorlesungstag {
        var newItems: ArrayList<VorlesungsplanItem> = ArrayList()
        for (elem in day.items) {

            val progress = getProgress(elem.startTime, elem.endTime, day.tag)
            log.info("Progress for ${day.tag} ${elem.title} is $progress")

            //for testing
            //val description = elem.description+"Progress: ${progress}"
            val description = elem.description

            newItems.add(
                VorlesungsplanItem(
                    elem.title,
                    elem.time,
                    description,
                    elem.startTime,
                    elem.endTime,
                    progress
                )
            )
        }
        return Vorlesungstag(day.tag, newItems, day.tagDate)
    }

    private fun readWebsite(url: String): Document? {
        return try {
            Jsoup.connect(url).get()
        } catch (e: Exception) {
            null
        }
    }

    private fun isolateTime(dateStringIn: String): Date {
        var dateString = dateStringIn
        if (dateString.contains(",")) {
            while (dateString.substring(0, 1) != ",") {
                dateString = dateString.substring(1)
            }
            dateString = dateString.substring(1)
        }
        return SimpleDateFormat("dd.MM").parse(dateString.trim())!!
    }

    private fun getTimes(times: String): Times {
        var t1: Date
        var t2: Date
        val format = SimpleDateFormat("HH:mm")
        try {
            t1 = format.parse(times.substring(0, 5))!!
            t2 = format.parse(times.substring(6, 11))!!
        } catch (e: Exception) {
            t1 = format.parse("00:00")!!
            t2 = format.parse("00:00")!!
        }

        return Times(
            t1,
            t2
        )
    }


    //returns 100 if the class is done, 0 if the class is in the future and a value from 0-100 if the class is the current class.
    // The value is the percentage of the progress of the class
    private fun getProgress(beg: Date, endTime: Date, day: String): Int {
        val formatter = SimpleDateFormat("dd.MM")
        val today: Date = formatter.parse(TimeEstimator().getTodayDate())!!
        val dayDate: Date = formatter.parse(day.substring(day.length - 5, day.length))!!
        val now = TimeEstimator()
            .getTodayMinutes()
        val begin = TimeEstimator()
            .getMinutes(beg)
        val end = TimeEstimator()
            .getMinutes(endTime)

        if (dayDate.after(today)) {
            return 0
        }
        if (dayDate.before(today)) {
            return 100
        }
        if (now < begin) {
            return 0
        }
        if (end < now) {
            return 100
        }
        return (((now - begin).toFloat() / (end - begin).toFloat()) * 100).toInt()
    }

    private class Times(val start: Date, val end: Date)
}