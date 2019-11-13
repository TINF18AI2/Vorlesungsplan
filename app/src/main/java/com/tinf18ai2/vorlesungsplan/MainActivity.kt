package com.tinf18ai2.vorlesungsplan

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager

import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import java.util.logging.Logger


class MainActivity : AppCompatActivity() {

    private lateinit var adapter: VorlesungsplanAdapter

    var log: Logger = Logger.getGlobal()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val linearLayoutManager = LinearLayoutManager(this)
        mainRecyclerView.layoutManager = linearLayoutManager
        setSupportActionBar(toolbar)
        DemoThread().start()
    }


    internal inner class DemoThread : Thread() {

        override fun run() {
            val data = getData()
            if(data != null){
                adapter = VorlesungsplanAdapter(items = data, context = applicationContext)
                mainRecyclerView.adapter = adapter
            }

        }

        private fun getData(): List<Vorlesungstag>? {    //reads out the Information from the Website and saves it in the returned Array
            val week: ArrayList<Vorlesungstag> =
                ArrayList<Vorlesungstag>()    //Holds Information about the hole week
            val site: Document =
                readWebsite("https://vorlesungsplan.dhbw-mannheim.de/index.php?action=view&gid=3067001&uid=7431001")  //This is the raw source code of the website

            val days = site.getElementsByClass("ui-grid-e").first().children()//Array which holds information about every day in the week

            for (day in days) {
                log.info("\n\nTest: "+day.toString())
                val items = ArrayList<VorlesungsplanItem>() //Every Vorlesung of the day
                for (elem in day.getElementsByClass("ui-li ui-li-static ui-body-b")) {
                    items.add(
                        VorlesungsplanItem(
                            elem.getElementsByClass("cal-title").first().text(),
                            elem.getElementsByClass("cal-time").first().text(),
                            elem.getElementsByClass("cal-res").first().text()
                        )
                    )
                }
                week.add(
                    Vorlesungstag(
                        day.getElementsByAttributeValue("data-role","list-divider").first().text(),
                        items
                    )
                )
            }
            log.info("Week: "+week.toString())
            return week

        }

        private fun readWebsite(url: String): Document {
            val jsup : Document = Jsoup.connect(url).get()
            //log.info("Text: "+jsup)
            return jsup
        }

    }
}