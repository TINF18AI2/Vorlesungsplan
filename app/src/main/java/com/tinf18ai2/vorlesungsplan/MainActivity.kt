package com.tinf18ai2.vorlesungsplan

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*
import java.util.logging.Logger


class MainActivity : AppCompatActivity() {

    var log: Logger = Logger.getGlobal()
    private lateinit var adapter: VorlesungsplanAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val linearLayoutManager = LinearLayoutManager(this)
        mainRecyclerView.layoutManager = linearLayoutManager
        setSupportActionBar(toolbar)

        var week = VorlesungsplanAnalyser().analyse()
        var allItems: ArrayList<VorlesungsplanItem> = ArrayList()

        for (day in week) {
            allItems.add(VorlesungsplanItem(day.tag,"",""))
            for (item in day.items) {
                allItems.add(item)
            }
        }
        log.info("wek: " + week.toString())
        log.info("allItems: " + allItems.toString())
        adapter = VorlesungsplanAdapter(items = allItems, context = applicationContext)
        mainRecyclerView.adapter = adapter

        //fab.setOnClickListener { v: View? ->
        //    val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(URL))
        //    startActivity(browserIntent)
        //}
    }
}