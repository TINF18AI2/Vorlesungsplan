package com.tinf18ai2.vorlesungsplan.backend_services

import com.tinf18ai2.vorlesungsplan.models.VorlesungsplanItem
import com.tinf18ai2.vorlesungsplan.models.Vorlesungstag
import java.lang.NullPointerException
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import java.util.logging.Logger

class TimeUntilUniEnd{

    var log: Logger = Logger.getGlobal()

    fun getTimeLeft(week: List<Vorlesungstag>) : UniAusErg? {

        var erg = vorlesungsEnde(week)
        if (erg==null){
            val today = getToday(week)
            if(today!=null){
                if(getCurrentClass(today)!=null){
                    return timeToNextClass(week)
                }
            }
        }
        return erg
    }

    fun getCurrentClass(today: Vorlesungstag): VorlesungsplanItem?{
        val now = getTodayMinutes()
        for (item in today.items){
            val beg = getMinutes(item.startTime)//getMinutes(today.select("ul").first().children().last().select("div.cal-time").text().substring(0,5))

            val end = getMinutes(item.endTime)//getMinutes(today.select("ul").first().children().last().select("div.cal-time").text().substring(6,11))

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

    private fun timeToNextClass(week: List<Vorlesungstag>): UniAusErg? {
        val todayDate : Date = SimpleDateFormat("dd.MM").parse(getTodayDate())
        var days: Int = 0
        for (day in week){
            if(!day.tagDate.before(todayDate)){
                for(item in day.items){
                    if (item.progress==0){
                        return timeTo(item,days)
                    }
                }
                days++
            }
        }

        return null
    }

    private fun timeTo(item: VorlesungsplanItem, days: Int) : UniAusErg? {
        var erg = timeToItem(item,false)
        if(erg!=null){
            while (erg.mins<0){
                erg.mins+=60
                erg.hours--
            }
            while(erg.hours<0){
                erg.hours+=24
                erg.days--
            }
            if (days<0){
                log.info("ERROR: Days <0 !!!! in TimeUntilUniEnd timeTo function")
            }
            erg.to = false
        }
        return erg
    }

    private fun uniAus(today: Vorlesungstag): UniAusErg? {
        val current = getCurrentClass(today)
        if(current!=null){
            return timeToItem(current,true)
        }
        return null
    }

    private fun timeToItem(item: VorlesungsplanItem, toEnd: Boolean) : UniAusErg? {
        var erg : UniAusErg =
            UniAusErg(-1, -1,-1,"")
        val now = getTodayMinutes()
        val allMins =
            if(toEnd){
                getMinutes(item.endTime) - now
            }else{
                getMinutes(item.startTime)-now
            }
        if(toEnd&&allMins<0){
            return null
        }
        erg.mins = allMins%60
        erg.hours = (allMins -erg.mins)/60
        erg.days = 0
        erg.name = item.title
        log.info("Time to: "+erg)
        return erg
    }

    private fun vorlesungsEnde(week: List<Vorlesungstag>): UniAusErg? {
        var todayElem : Vorlesungstag? = getToday(week)
        try {
            return uniAus(todayElem!!)
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return null
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

class UniAusErg(var days: Int,var hours: Int, var mins: Int, var name: String, var to: Boolean = true) //Holds information about the Current Vorlesung and the time left until end