package com.tinf18ai2.vorlesungsplan

import android.os.Bundle
import android.view.View.INVISIBLE
import android.view.View.VISIBLE
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*
import java.util.logging.Logger


class MainActivity : AppCompatActivity() {

    companion object {
        val LOG: Logger = Logger.getGlobal()
    }

    private lateinit var adapter: VorlesungsplanAdapter

    var woche: List<Vorlesungstag> = ArrayList<Vorlesungstag>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val linearLayoutManager = LinearLayoutManager(this)
        mainRecyclerView.layoutManager = linearLayoutManager
        setSupportActionBar(toolbar)

        fab.setOnClickListener {
            EstimateTimeLest(woche = woche, timeResultCallback = object : TimeResultCallback {
                override fun onFinished(time: String) {
                    showTimeLeft(time)
                }
            }).execute()
        }
        reloadViews()

    }

    private fun reloadViews() {
        mainRecyclerView.visibility = INVISIBLE
        progressBar.visibility = VISIBLE
        LoadData(weekDataCallback = object : WeekDataCallback {
            override fun onDataRecieved(list: List<Vorlesungstag>) {
                woche = list
                mainRecyclerView.visibility = VISIBLE
                progressBar.visibility = INVISIBLE
                adapter = VorlesungsplanAdapter(
                    items = ListItemProvider.getAllListItems(list),
                    context = applicationContext
                )
                mainRecyclerView.adapter = adapter
            }
        }).execute()

    }

    override fun onResume() {
        super.onResume()
        reloadViews()
    }


    fun showTimeLeft(time: String) {
        Snackbar.make(mainView, time, Snackbar.LENGTH_LONG)
            .show()
    }
}