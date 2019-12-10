package com.tinf18ai2.vorlesungsplan.backend_services.lecture_plan_modules

import com.tinf18ai2.vorlesungsplan.backend_services.time_estimation.TimeEstimator
import com.tinf18ai2.vorlesungsplan.models.VorlesungsplanItem
import com.tinf18ai2.vorlesungsplan.models.Vorlesungstag
import com.tinf18ai2.vorlesungsplan.ui.MainActivity.Companion.LOG
import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.BehaviorSubject
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import java.text.SimpleDateFormat
import java.util.*
import java.util.logging.Logger
import kotlin.collections.ArrayList


object PlanAnalyser {

    private val input = BehaviorSubject.create<Int>()
    private val output = BehaviorSubject.create<List<Vorlesungstag>>()

    init {
        input.observeOn(Schedulers.io())            // Run next step in IO-Threads
            .map { download(it) }                   // Download data
            .observeOn(Schedulers.computation())    // Run next step in Compute-Threads
            .map { parse(it) }                      // Parse data
            .subscribe(output)                      // Pipe to output
    }

    val URL =
        "https://vorlesungsplan.dhbw-mannheim.de/index.php?action=view&gid=3067001&uid=7431001"
    val ICAL_URL = "http://vorlesungsplan.dhbw-mannheim.de/ical.php?uid=7431001"

    fun toObservable(): Observable<List<Vorlesungstag>> {
        return output
    }

    fun analyse(shiftWeeks: Int) {
        input.onNext(shiftWeeks)
    }

    private fun calculateTimeStamp(shiftWeeks: Int): String {
        val shiftDays = 7 * shiftWeeks
        val base = "&date="
        val dayMillis = 24 * 60 * 60
        val time = Date().time / 1000 + (1 + shiftDays) * dayMillis
        return base + time
    }

    /**
     * Downloads the needed data
     */
    private fun download(shiftWeeks: Int): Document {
        return readWebsite(URL + calculateTimeStamp(shiftWeeks))
    }

    /**
     * Parses the data given in the Document and returns a List of Vorlesungstag
     */
    private fun parse(site: Document): List<Vorlesungstag> {
        // Holds Information about the hole week
        val week: ArrayList<Vorlesungstag> = ArrayList()
        // Array which holds information about every day in the week
        val days = site.getElementsByClass("ui-grid-e").first().children()

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
                val tag =
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
        val newItems: ArrayList<VorlesungsplanItem> = ArrayList()
        for (elem in day.items) {

            val progress = getProgress(elem.startTime, elem.endTime, day.tag)
            LOG.info("Progress for ${day.tag} ${elem.title} is $progress")

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

    /**
     * Reads the website from the given URL.
     *
     * @throws java.net.MalformedURLException if the request URL is not a HTTP or HTTPS URL, or is otherwise malformed
     * @throws org.jsoup.HttpStatusException if the response is not OK and HTTP response errors are not ignored
     * @throws org.jsoup.UnsupportedMimeTypeException if the response mime type is not supported and those errors are not ignored
     * @throws java.net.SocketTimeoutException if the connection times out
     * @throws java.io.IOException on error
     */
    private fun readWebsite(url: String): Document {
        return Jsoup.connect(url).get()
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

    /**
     * Returns the progress of the class in percent.
     *
     * @return 100 if the class is done, 0 if the class is in the future, 1-99 current progress
     */
    private fun getProgress(beg: Date, endTime: Date, day: String): Int {
        val formatter = SimpleDateFormat("dd.MM")
        val today: Date = formatter.parse(TimeEstimator.getTodayDateString())!!
        val dayDate: Date = formatter.parse(day.substring(day.length - 5, day.length))!!
        val now = TimeEstimator.getMinutesOfToday()
        val begin = TimeEstimator.getMinutesOfDay(beg)
        val end = TimeEstimator.getMinutesOfDay(endTime)

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