package com.tinf18ai2.vorlesungsplan.backend_services

import com.tinf18ai2.vorlesungsplan.models.VorlesungsplanItem
import com.tinf18ai2.vorlesungsplan.models.Vorlesungstag
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import java.lang.Exception
import java.text.SimpleDateFormat
import java.util.*
import java.util.logging.Logger
import kotlin.collections.ArrayList


class AsyncPlanAnalyser {

    val URL = "https://vorlesungsplan.dhbw-mannheim.de/index.php?action=view&gid=3067001&uid=7431001"
    val ICAL_URL = "http://vorlesungsplan.dhbw-mannheim.de/ical.php?uid=7431001"

    var log: Logger = Logger.getGlobal()

    fun analyse(): List<Vorlesungstag>? {
        return getData(0)
    }

    fun analyse(shiftWeeks: Int): List<Vorlesungstag>? {
        return getData(shiftWeeks)
    }

    private fun getIcalData(): List<Vorlesungstag>?{
        val week: ArrayList<Vorlesungstag> = ArrayList()

        return week
    }

    private fun calculateTimeStamp(shiftWeeks: Int) : String{
        var shiftDays = 7*shiftWeeks
        val base = "&date="
        val dayMillis = 24*60*60
        var time = Date().time / 1000 + (1+shiftDays)*dayMillis
        return base+time
    }

    private fun getData(shiftWeeks: Int): List<Vorlesungstag>? {    //reads out the Information from the Website and saves it in the returned Array
        val week: ArrayList<Vorlesungstag> = ArrayList()    //Holds Information about the hole week
        val site: Document = readWebsite(URL+calculateTimeStamp(shiftWeeks)) ?: return null  //returns null if site==null

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
                            val info: String =
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
                                    false
                                )
                            )
                        }

                    }
                }
            }

            if (day.getElementsByAttributeValue("data-role", "list-divider").isNotEmpty()) {
                val dayString: String =
                    day.getElementsByAttributeValue("data-role", "list-divider").first().text()
                week.add(
                    Vorlesungstag(
                        dayString,
                        items,
                        isolateTime(dayString)
                    )
                )
            }
        }
        return week

    }

    private fun readWebsite(url: String): Document? {
        return try {
            Jsoup.connect(url).get()
        }catch (e : Exception){
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
        return SimpleDateFormat("dd.MM").parse(dateString.trim())
    }

    private fun getTimes(times: String) : Times {
        val t1 : Date = SimpleDateFormat("HH:mm").parse(times.substring(0,5))
        val t2 : Date = SimpleDateFormat("HH:mm").parse(times.substring(6,11))

        return Times(t1, t2)
    }

    private class Times(val start: Date,val end: Date)
}