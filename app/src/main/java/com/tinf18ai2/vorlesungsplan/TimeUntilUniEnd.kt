package com.tinf18ai2.vorlesungsplan

import android.content.Context
import android.widget.Toast
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element
import org.jsoup.select.Elements
import java.lang.NullPointerException
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import java.util.logging.Logger

class TimeUntilUniEnd(val context: Context){

    var log: Logger = Logger.getGlobal()
    var end : String? = null

    fun show(){
        log.info("clicked")
        ShowTimeLeft().start()
        while (end == null){

        }
        log.info("Left time: "+end)
        Toast.makeText(context,end,Toast.LENGTH_LONG)
    }

    private fun showTime(time : String){
    }
    internal inner class ShowTimeLeft : Thread() {

        override fun run() {
            val wann = vorlesungsEnde()
            var ende = ""
            if (wann < 0) {
                ende = "Zurzeit findet keine Vorlesung für den Kurs TINF-18 AI2 statt."
            } else {
                ende = "Noch $wann Minuten bis zum Vorlesungsende!"
            }
            end = ende
            this.interrupt()
        }
    }
    fun vorlesungsEnde(): Int {
        val formatter = SimpleDateFormat("dd.MM")
        val date = Date(System.currentTimeMillis())
        val todayDate : String = formatter.format(date)
        var todayElem : Element? = getToday(todayDate)
        try {
            return uniAus(todayElem!!)
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return -1
    }
    private fun uniAus(today: Element): Int {
        var erg = -1
        try {
            val beg = getMinutes(today.select("ul").first().children().last().select("div.cal-time").text().substring(0,5))
            log.info("Begin: "+beg)
            val end = getMinutes(today.select("ul").first().children().last().select("div.cal-time").text().substring(6,11))
            log.info("until: "+end)
            val now = getTodayMinutes()
            if (beg <= now && end >= now) {
                erg = end - now
            }
        }catch (e : NullPointerException){
            erg = -1
        }
        log.info("Time left: "+erg)
        return erg
    }
    private fun getMinutes(time: String): Int {
        var date1: Date? = null
        try {
            date1 = SimpleDateFormat("HH:mm").parse(time)
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
    private fun getTodayMinutes(): Int {
        val format = SimpleDateFormat("HH:mm")
        return getMinutes(format.format(Date(System.currentTimeMillis())))
    }

    fun getDays() : Elements {
        val site: Document = Jsoup.connect(VorlesungsplanAnalyser().URL).get()

        val days = site.getElementsByClass("ui-grid-e").first()
            .children()//Array which holds information about every day in the week
        return days
    }
    fun getToday(date : String) : Element? {
        var today : Element? = null
        for(day in getDays()){
            if(day.getElementsContainingText(date).isNotEmpty()){
                today = day
                break
            }
        }
        return today
    }
}