package com.tinf18ai2.vorlesungsplan.services.impl

import com.tinf18ai2.vorlesungsplan.models.VorlesungsplanItem
import com.tinf18ai2.vorlesungsplan.models.Vorlesungstag
import com.tinf18ai2.vorlesungsplan.models.Vorlesungswoche
import com.tinf18ai2.vorlesungsplan.services.LecturePlanFetchService
import com.tinf18ai2.vorlesungsplan.services.ServiceFactory
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


class LecturePlanFetchServiceImpl : LecturePlanFetchService {

    companion object {
        const val URL = "https://vorlesungsplan.dhbw-mannheim.de/index.php?action=view&uid=7431001"
        const val ICAL_URL = "http://vorlesungsplan.dhbw-mannheim.de/ical.php?uid=7431001"
    }

    override fun fetch(weekOffset: Int): Single<Vorlesungswoche> {
        return Single.just(weekOffset)
            .observeOn(Schedulers.io())             // Run next step in IO-Threads
            .map { download(it) }                   // Download data
            .observeOn(Schedulers.computation())    // Run next step in Compute-Threads
            .map { parse(it) }                      // Parse data
    }

    /**
     * Calculate timestamp in relation to the week offset
     */
    private fun calculateTimeStamp(weekOffset: Int): String {
        val shiftDays = 7 * weekOffset
        val dayMillis = 24 * 60 * 60
        val time = Date().time / 1000 + (1 + shiftDays) * dayMillis
        return "&date=$time"
    }

    /**
     * Downloads the needed data
     */
    private fun download(weekOffset: Int): AugmentedDocument {
        return AugmentedDocument(weekOffset, readWebsite(URL + calculateTimeStamp(weekOffset)))
    }

    /**
     * Parses the data given in the Document and returns a List of Vorlesungstag
     */
    private fun parse(augmentedDocument: AugmentedDocument): Vorlesungswoche {
        // Holds Information about the hole week
        val week: ArrayList<Vorlesungstag> = ArrayList()
        // Array which holds information about every day in the week
        val days = augmentedDocument.document.getElementsByClass("ui-grid-e").first().children()

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
                            val times =
                                getTimes(
                                    timeString
                                )
                            val info: String? =
                                if (elem.getElementsByClass("cal-res").isEmpty()) {
                                    elem.getElementsByClass("cal-text")?.first()?.text()
                                } else {
                                    elem.getElementsByClass("cal-res")?.first()?.text()
                                }
                            items.add(
                                VorlesungsplanItem(
                                    elem.getElementsByClass("cal-title").first().text(),
                                    timeString,
                                    info.orEmpty(),
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
                        isolateTime(
                            dayString
                        )
                    )
                week.add(
                    setProgresses(
                        tag
                    )
                )
            }
        }
        return Vorlesungswoche(augmentedDocument.offsetWeek, week)
    }

    private fun setProgresses(day: Vorlesungstag): Vorlesungstag {
        val newItems: ArrayList<VorlesungsplanItem> = ArrayList()
        for (elem in day.items) {
            newItems.add(
                VorlesungsplanItem(
                    elem.title,
                    elem.time,
                    elem.description,
                    elem.startTime,
                    elem.endTime,
                    getProgress(
                        elem.startTime,
                        elem.endTime,
                        day.tag
                    )
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
        return SimpleDateFormat("dd.MM", ServiceFactory.getLocale().getDisplayLocale()).parse(
            dateString.trim()
        )!!
    }

    private fun getTimes(times: String): Times {
        var t1: Date
        var t2: Date
        val format = SimpleDateFormat("HH:mm", ServiceFactory.getLocale().getDisplayLocale())
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
        val timeEstimation = ServiceFactory.getTimeEstimation()

        val formatter = SimpleDateFormat("dd.MM", Locale.GERMANY)
        val today: Date = formatter.parse(timeEstimation.getTodayDateString())!!
        val dayDate: Date = formatter.parse(day.substring(day.length - 5, day.length))!!
        val now = timeEstimation.getMinutesOfToday()
        val begin = timeEstimation.getMinutesOfDay(beg)
        val end = timeEstimation.getMinutesOfDay(endTime)

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

    private class AugmentedDocument(val offsetWeek: Int, val document: Document)
    private class Times(val start: Date, val end: Date)
}