package com.tinf18ai2.vorlesungsplan

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*
import org.jsoup.Jsoup
import org.jsoup.nodes.Document

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)
        displayPlan()
    }

    private fun displayPlan(){
        var site : Document = readWebsite("https://vorlesungsplan.dhbw-mannheim.de/index.php?action=view&gid=3067001&uid=7431001")

        var days = site.getElementsByClass("ui-grid-e").first().allElements

        for(day in days){

        }
    }

    private fun readWebsite(url:String) :  Document{
        return Jsoup.connect(url).get()
    }
}