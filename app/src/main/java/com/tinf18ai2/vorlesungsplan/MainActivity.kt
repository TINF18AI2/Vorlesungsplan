package com.tinf18ai2.vorlesungsplan

import android.os.AsyncTask
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*
import java.text.SimpleDateFormat
import java.util.*
import java.util.logging.Logger
import kotlin.collections.ArrayList


class MainActivity : AppCompatActivity() {

    var log: Logger = Logger.getGlobal()
    private lateinit var adapter: VorlesungsplanAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val linearLayoutManager = LinearLayoutManager(this)
        mainRecyclerView.layoutManager = linearLayoutManager
        setSupportActionBar(toolbar)

        //fab.setOnClickListener { v: View? ->
        //    val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(URL))
        //    startActivity(browserIntent)
        //}

        LoadData().execute()
    }

    override fun onResume() {
        super.onResume()
        LoadData().execute()
    }

    fun showPlan(week: List<Vorlesungstag>){
        var allItems: ArrayList<VorlesungsplanItem> = ArrayList()

        for (day in week) {
            allItems.add(VorlesungsplanItem(day.tag,"","", SimpleDateFormat("dd.MM").parse("00.00"),SimpleDateFormat("dd.MM").parse("00.00")))
            for (item in day.items) {
                allItems.add(item)
            }
        }
        log.info("wek: " + week.toString())
        log.info("allItems: " + allItems.toString())
        adapter = VorlesungsplanAdapter(items = allItems, context = applicationContext)
        mainRecyclerView.adapter = adapter
    }

    private inner class LoadData : AsyncTask<Void, Void, List<Vorlesungstag>>() {
        override fun doInBackground(vararg p0: Void?): List<Vorlesungstag> {
            return AsyncPlanAnalyser().analyse()
        }

        override fun onPostExecute(result: List<Vorlesungstag>?) {
            super.onPostExecute(result)
            if (result != null) {
                showPlan(result)
            }
        }
    }

}