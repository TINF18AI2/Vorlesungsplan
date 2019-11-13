package com.tinf18ai2.vorlesungsplan

import android.os.AsyncTask
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import java.util.logging.Logger


class AsyncPlanAnalyser {

    private var URL =
        "https://vorlesungsplan.dhbw-mannheim.de/index.php?action=view&gid=3067001&uid=7431001"

    var wek: List<Vorlesungstag> = ArrayList<Vorlesungstag>()
    var log: Logger = Logger.getGlobal()

    fun analyse() : List<Vorlesungstag>{

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
            log.info("\n\nDay: " + day.toString())
            val items = ArrayList<VorlesungsplanItem>() //Every Vorlesung of the day
            var first : Boolean = true
            if(day.select("ul").isNotEmpty()){
                if(day.select("ul").first().children().isNotEmpty()){
                    for (elem in day.select("ul").first().children()) {

                        if (first){
                            first = false
                        }else{
                            log.info("\n\nItem: " + elem.toString())
                            var info : String = ""
                            if (elem.getElementsByClass("cal-res").isEmpty()){
                                info = elem.getElementsByClass("cal-text").first().text()
                            }else{
                                info = elem.getElementsByClass("cal-res").first().text()
                            }
                            items.add(
                                VorlesungsplanItem(
                                    elem.getElementsByClass("cal-title").first().text(),
                                    elem.getElementsByClass("cal-time").first().text(),
                                    info
                                )
                            )
                        }

                    }
                }
            }

            if(day.getElementsByAttributeValue("data-role", "list-divider").isNotEmpty()){
                week.add(
                    Vorlesungstag(
                        day.getElementsByAttributeValue("data-role", "list-divider").first().text(),
                        items
                    )
                )
            }
        }
        log.info("Week: " + week.toString())
        return week

    }

    private fun readWebsite(url: String): Document {
        val jsup: Document = Jsoup.connect(url).get()
        //log.info("Text: "+jsup)
        return jsup
    }
}