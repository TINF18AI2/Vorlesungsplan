package com.tinf18ai2.vorlesungsplan

import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import java.text.SimpleDateFormat
import java.util.*
import java.util.logging.Logger
import kotlin.collections.ArrayList


class AsyncPlanAnalyser {

    val URL =
        "https://vorlesungsplan.dhbw-mannheim.de/index.php?action=view&gid=3067001&uid=7431001"

    var log: Logger = Logger.getGlobal()

    fun analyse(): List<Vorlesungstag> {

        var data = getData()
        return data
    }

    private fun getData(): List<Vorlesungstag> {    //reads out the Information from the Website and saves it in the returned Array
        val week: ArrayList<Vorlesungstag> =
            ArrayList<Vorlesungstag>()    //Holds Information about the hole week
        val site: Document =
            readWebsite(URL)  //This is the raw source code of the website

        val days = site.getElementsByClass("ui-grid-e").first()
            .children()//Array which holds information about every day in the week

        for (day in days) {
            val items = ArrayList<VorlesungsplanItem>() //Every Vorlesung of the day
            var first: Boolean = true
            if (day.select("ul").isNotEmpty()) {
                if (day.select("ul").first().children().isNotEmpty()) {
                    for (elem in day.select("ul").first().children()) {

                        if (first) {
                            first = false
                        } else {
                            var timeString = elem.getElementsByClass("cal-time").first().text()
                            var times = getTimes(timeString)
                            var info: String = ""
                            if (elem.getElementsByClass("cal-res").isEmpty()) {
                                info = elem.getElementsByClass("cal-text").first().text()
                            } else {
                                info = elem.getElementsByClass("cal-res").first().text()
                            }
                            items.add(
                                VorlesungsplanItem(
                                    elem.getElementsByClass("cal-title").first().text(),
                                    timeString,
                                    info,
                                    times.start,
                                    times.end
                                )
                            )
                        }

                    }
                }
            }

            if (day.getElementsByAttributeValue("data-role", "list-divider").isNotEmpty()) {
                var dayString: String =
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

    private fun readWebsite(url: String): Document {
        val jsup: Document = Jsoup.connect(url).get()
        return jsup
    }

    private fun isolateTime(dateString: String): Date {
        var dateString = dateString
        if (dateString.contains(",")) {
            while (dateString.substring(0, 1) != ",") {
                dateString = dateString.substring(1)
            }
            dateString = dateString.substring(1)
        }
        var date : Date = SimpleDateFormat("dd.MM").parse(dateString.trim())
        return date
    }

    private fun getTimes(times: String) : Times{
        var t1 : Date = SimpleDateFormat("HH:mm").parse(times.substring(0,5))
        var t2 : Date = SimpleDateFormat("HH:mm").parse(times.substring(6,11))

        return Times(t1,t2)
    }

    private class Times(val start: Date,val end: Date)
}