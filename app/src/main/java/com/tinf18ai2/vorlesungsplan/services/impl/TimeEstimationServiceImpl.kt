package com.tinf18ai2.vorlesungsplan.services.impl

import com.tinf18ai2.vorlesungsplan.exceptions.NoLectureException
import com.tinf18ai2.vorlesungsplan.exceptions.NoLecturePlanWeekException
import com.tinf18ai2.vorlesungsplan.models.FABDataModel
import com.tinf18ai2.vorlesungsplan.models.VorlesungsplanItem
import com.tinf18ai2.vorlesungsplan.models.Vorlesungstag
import com.tinf18ai2.vorlesungsplan.services.ServiceFactory
import com.tinf18ai2.vorlesungsplan.services.TimeEstimationService
import com.tinf18ai2.vorlesungsplan.ui.MainActivity.Companion.LOG
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

class TimeEstimationServiceImpl : TimeEstimationService {

    override fun estimate(): Single<FABDataModel> {
        return Single.just(
            ServiceFactory.getLecturePlan().getCurrentWeek()
        )
            .onErrorReturn {
                throw NoLecturePlanWeekException("Unable to get current week from lecture plan")
            }
            .observeOn(Schedulers.computation())
            .map {
                getFABData(it.days)
                    ?: throw NoLectureException("Unable to get current or next lecture")
            }
    }

    private fun getFABData(week: List<Vorlesungstag>): FABDataModel? {
        val erg = timeToEndOfCurrentClass(week)
        if (erg == null) {
            val today = getToday(week)
            if (today != null) {
                return timeToNextClass(week)
            }
        }
        return erg
    }

    private fun getCurrentClassOfToday(today: Vorlesungstag): VorlesungsplanItem? {
        val now = getMinutesOfToday()
        for (item in today.items) {
            val beg = getMinutesOfDay(item.startTime)
            val end = getMinutesOfDay(item.endTime)

            if (now in beg..end) {
                return item
            }
        }
        return null
    }

    override fun getMinutesOfDay(beg: Date): Int {
        try {
            val hours = SimpleDateFormat("HH", ServiceFactory.getLocale().getDisplayLocale())
            val minutes = SimpleDateFormat("mm", ServiceFactory.getLocale().getDisplayLocale())
            return Integer.parseInt(hours.format(beg)) * 60 + Integer.parseInt(minutes.format(beg))
        } catch (e: ParseException) {
            e.printStackTrace()
        }
        return 0
    }

    private fun getMinutesOfDay(time: String): Int {
        return getMinutesOfDay(
            SimpleDateFormat(
                "HH:mm",
                ServiceFactory.getLocale().getDisplayLocale()
            ).parse(time)!!
        )
    }

    override fun getMinutesOfToday(): Int {
        val format = SimpleDateFormat("HH:mm", ServiceFactory.getLocale().getDisplayLocale())
        return getMinutesOfDay(format.format(Date(System.currentTimeMillis())))
    }

    private fun timeToNextClass(week: List<Vorlesungstag>): FABDataModel? {
        val todayDate: Date =
            SimpleDateFormat("dd.MM", ServiceFactory.getLocale().getDisplayLocale()).parse(
                getTodayDateString()
            )!!
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
                LOG.severe("ERROR: Days <0 !!!! in TimeUntilUniEnd timeTo function")
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
        val erg = FABDataModel(item.endTime.time, -1, -1, -1, "")
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
        val formatter = SimpleDateFormat("dd.MM", ServiceFactory.getLocale().getDisplayLocale())
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

    override fun getTodayDateString(): String {
        val formatter = SimpleDateFormat("dd.MM", ServiceFactory.getLocale().getDisplayLocale())
        val date = Date(System.currentTimeMillis())
        return formatter.format(date)
    }
}

