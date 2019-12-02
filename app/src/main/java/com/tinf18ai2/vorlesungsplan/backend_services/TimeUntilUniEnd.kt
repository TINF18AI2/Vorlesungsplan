package com.tinf18ai2.vorlesungsplan.backend_services

import com.tinf18ai2.vorlesungsplan.models.VorlesungsplanItem
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
        var todayElem : Vorlesungstag? = getToday(week)
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
            val current = getCurrentClass(today)
            if(current!=null){
                erg.timeLeft = getMinutes(current.endTime) - now
                erg.name = current.title
            }
        }catch (e : NullPointerException){
            erg.timeLeft = -1
        }
        log.info("Time left: "+erg)
        return erg
    }

    fun getCurrentClass(today: Vorlesungstag): VorlesungsplanItem?{
        val now = getTodayMinutes()
        for (item in today.items){
            val beg = getMinutes(item.startTime)//getMinutes(today.select("ul").first().children().last().select("div.cal-time").text().substring(0,5))
            log.info("Begin: $beg")
            val end = getMinutes(item.endTime)//getMinutes(today.select("ul").first().children().last().select("div.cal-time").text().substring(6,11))
            log.info("until: $end")
            if (now in beg..end) {
                return item
            }
        }
        return null
    }

    fun getMinutes(time: Date): Int{
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
    fun getMinutes(time: String): Int {
        return getMinutes(SimpleDateFormat("HH:mm").parse(time))
    }

    fun getTodayMinutes(): Int {
        val format = SimpleDateFormat("HH:mm")
        return getMinutes(format.format(Date(System.currentTimeMillis())))
        //return(getMinutes("11:00"))//For testing
    }

    private fun getToday(week: List<Vorlesungstag>) : Vorlesungstag? {
        val formatter = SimpleDateFormat("dd.MM")
        var today : Vorlesungstag? = null
        val todayDate : String = getTodayDate()
        for(day in week){
            if(formatter.format(day.tagDate)==todayDate){
                today = day
                break
            }
        }
        return today
    }

    fun getTodayDate():String{
        val formatter = SimpleDateFormat("dd.MM")
        val date = Date(System.currentTimeMillis())
        val todayDate : String = formatter.format(date)
        return todayDate
    }
}

class UniAusErg(var timeLeft: Int, var name: String) //Holds information about the Current Vorlesung and the time left until end