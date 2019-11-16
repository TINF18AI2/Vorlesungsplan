package com.tinf18ai2.vorlesungsplan.backend_services

import com.tinf18ai2.vorlesungsplan.models.Vorlesungstag
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import java.util.logging.Logger

class TimeUntilUniEnd{

    var log: Logger = Logger.getGlobal()

    fun getTimeLeft(week: List<Vorlesungstag>) : UniAusErg {
        return vorlesungsEnde(week)
    }
    private fun vorlesungsEnde(week: List<Vorlesungstag>): UniAusErg {
        val formatter = SimpleDateFormat("dd.MM")
        val date = Date(System.currentTimeMillis())
        val todayDate : String = formatter.format(date)
        var todayElem : Vorlesungstag? = getToday(todayDate,week,formatter)
        try {
            return uniAus(todayElem!!)
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return UniAusErg(-1, "")
    }

    private fun uniAus(today: Vorlesungstag): UniAusErg {
        var erg : UniAusErg =
            UniAusErg(-1, "")
        try {
            val now = getTodayMinutes()
            for (item in today.items){
                val beg = getMinutes(item.startTime)//getMinutes(today.select("ul").first().children().last().select("div.cal-time").text().substring(0,5))
                log.info("Begin: "+beg)
                val end = getMinutes(item.endTime)//getMinutes(today.select("ul").first().children().last().select("div.cal-time").text().substring(6,11))
                log.info("until: "+end)
                if (beg <= now && end >= now) {
                    erg.timeLeft = end - now
                    erg.name = item.title
                    break
                }
            }
        }catch (e : NullPointerException){
            erg.timeLeft = -1
        }
        log.info("Time left: "+erg)
        return erg
    }
    private fun getMinutes(time: Date): Int{
        var date1: Date? = time
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
    private fun getMinutes(time: String): Int {
        return getMinutes(SimpleDateFormat("HH:mm").parse(time))
    }
    private fun getTodayMinutes(): Int {
        val format = SimpleDateFormat("HH:mm")
        return getMinutes(format.format(Date(System.currentTimeMillis())))
        //return(getMinutes("11:00"))//For testing
    }

    private fun getToday(date : String, week: List<Vorlesungstag>, format: SimpleDateFormat) : Vorlesungstag? {
        var today : Vorlesungstag? = null
        for(day in week){
            if(format.format(day.tagDate)==date){
                today = day
                break
            }
        }
        return today
    }

}

class UniAusErg(var timeLeft: Int, var name: String) //Holds information about the Current Vorlesung and the time left until end