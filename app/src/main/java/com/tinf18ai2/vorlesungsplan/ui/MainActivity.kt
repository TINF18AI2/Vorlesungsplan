package com.tinf18ai2.vorlesungsplan.ui

import android.os.Bundle
import android.view.View.INVISIBLE
import android.view.View.VISIBLE
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.tinf18ai2.vorlesungsplan.backend_services.*
import com.tinf18ai2.vorlesungsplan.models.Vorlesungstag
import com.tinf18ai2.vorlesungsplan.R
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*
import java.util.logging.Logger


class MainActivity : AppCompatActivity() {

    companion object {
        val LOG: Logger = Logger.getGlobal()
    }

    private lateinit var adapter: VorlesungsplanAdapter
    private var networkError: Boolean = false
    private var weekShift = 0

    var woche: List<Vorlesungstag> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val linearLayoutManager = LinearLayoutManager(this)
        mainRecyclerView.layoutManager = linearLayoutManager
        setSupportActionBar(toolbar)
        mainRecyclerView.visibility = INVISIBLE
        progressBar.visibility = VISIBLE
        StateData.addSubscriber(subscriber = object : StateSubscriber {
            override fun onDataRecieved(list: List<Vorlesungstag>?) {
                if (list == null) {
                    makeSnackBar(getString(R.string.network_error_msg))
                    networkError = true
                } else {
                    networkError = false
                    woche = list
                    mainRecyclerView.visibility = VISIBLE
                    progressBar.visibility = INVISIBLE
                    adapter = VorlesungsplanAdapter(
                        items = ListItemProvider.getAllListItems(
                            list
                        ),
                        context = applicationContext
                    )
                    mainRecyclerView.adapter = adapter
                }
            }
        })

        fab.setOnClickListener {
            if (!networkError) {
                EstimateTimeLeft(
                    woche = woche,
                    timeResultCallback = object :
                        TimeResultCallback {
                        override fun onFinished(time: UniAusErg) {
                            showTimeLeft(time)
                        }
                    }).execute()
            } else {
                StateData.reloadData(weekShift)
            }
        }

        lastWeekButton.setOnClickListener {
            changeWeek(-1)
        }

        nextWeekButton.setOnClickListener {
            changeWeek(1)
        }
        currentWeekButton.setOnClickListener {
            changeWeek(0)
        }
        StateData.reloadData(weekShift)
    }

    private fun changeWeek(value: Int) {
        if (value == 0) {
            weekShift = 0
        }
        weekShift += value
        StateData.reloadData(weekShift)
    }


    override fun onResume() {
        super.onResume()
        StateData.reloadData(weekShift)
    }


    fun showTimeLeft(timeWhen: UniAusErg) {
        val end =
            if (timeWhen.timeLeft < 0) {
                getString(R.string.no_class_msg)
            } else {
                getString(R.string.time_left_msg, timeWhen.timeLeft, timeWhen.name)
            }
        makeSnackBar(end)
    }

    fun makeSnackBar(msg: String) {
        Snackbar.make(mainView, msg, Snackbar.LENGTH_LONG).show()
    }
}