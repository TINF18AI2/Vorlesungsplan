package com.tinf18ai2.vorlesungsplan.ui

import android.os.Bundle
import android.view.View.INVISIBLE
import android.view.View.VISIBLE
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.tinf18ai2.vorlesungsplan.R
import com.tinf18ai2.vorlesungsplan.backend_services.lecture_plan_modules.ListItemConverter
import com.tinf18ai2.vorlesungsplan.backend_services.lecture_plan_modules.LoadPlanObserver
import com.tinf18ai2.vorlesungsplan.backend_services.lecture_plan_modules.StateSubscriber
import com.tinf18ai2.vorlesungsplan.backend_services.time_estimation.EstimateTimeLeft
import com.tinf18ai2.vorlesungsplan.backend_services.time_estimation.TimeResultCallback
import com.tinf18ai2.vorlesungsplan.models.FABDataModel
import com.tinf18ai2.vorlesungsplan.models.Vorlesungstag
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
    var currentWeek: List<Vorlesungstag> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val linearLayoutManager = LinearLayoutManager(this)
        mainRecyclerView.layoutManager = linearLayoutManager
        setSupportActionBar(toolbar)
        mainRecyclerView.visibility = INVISIBLE
        progressBar.visibility = VISIBLE
        LoadPlanObserver.addSubscriber(subscriber = object :
            StateSubscriber {
            override fun onDataRecieved(list: List<Vorlesungstag>?) {
                if (list == null) {
                    makeSnackBar(getString(R.string.network_error_msg))
                    networkError = true
                } else {
                    networkError = false
                    woche = list
                    if (weekShift == 0) {
                        currentWeek = list
                    }
                    mainRecyclerView.visibility = VISIBLE
                    progressBar.visibility = INVISIBLE
                    adapter = VorlesungsplanAdapter(
                        items = ListItemConverter.getAllListItems(
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
                    woche = currentWeek,
                    timeResultCallback = object :
                        TimeResultCallback {
                        override fun onFinished(time: FABDataModel?) {
                            showTimeLeft(time)
                        }
                    }).execute()
            } else {
                LoadPlanObserver.reloadData(weekShift)
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
        LoadPlanObserver.reloadData(weekShift)
    }

    private fun changeWeek(value: Int) {
        if (value == 0) {
            weekShift = 0
        }
        weekShift += value
        LoadPlanObserver.reloadData(weekShift)
    }


    override fun onResume() {
        super.onResume()
        LoadPlanObserver.reloadData(weekShift)
    }


    fun showTimeLeft(timeWhen: FABDataModel?) {

        val end =
            if (timeWhen == null) {
                getString(R.string.no_class_msg)
            } else {
                if (timeWhen.hours > 0) {
                    if (timeWhen.days > 0) {
                        if (timeWhen.to) {
                            getString(
                                R.string.time_left_msg_mhd,
                                timeWhen.days,
                                timeWhen.hours,
                                timeWhen.mins,
                                timeWhen.name
                            )
                        } else {
                            getString(
                                R.string.time_to_msg_mhd,
                                timeWhen.days,
                                timeWhen.hours,
                                timeWhen.mins,
                                timeWhen.name
                            )
                        }
                    } else {
                        if (timeWhen.to) {
                            getString(
                                R.string.time_left_msg_mh,
                                timeWhen.hours,
                                timeWhen.mins,
                                timeWhen.name
                            )
                        } else {
                            getString(
                                R.string.time_to_msg_mh,
                                timeWhen.hours,
                                timeWhen.mins,
                                timeWhen.name
                            )
                        }
                    }
                } else {
                    if (timeWhen.to) {
                        getString(R.string.time_left_msg_m, timeWhen.mins, timeWhen.name)
                    } else {
                        getString(R.string.time_to_msg_m, timeWhen.mins, timeWhen.name)
                    }
                }
            }
        makeSnackBar(end)
    }

    fun makeSnackBar(msg: String) {
        Snackbar.make(mainView, msg, Snackbar.LENGTH_LONG).show()
    }
}