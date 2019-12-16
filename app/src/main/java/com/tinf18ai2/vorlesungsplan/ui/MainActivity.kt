package com.tinf18ai2.vorlesungsplan.ui

import android.content.Intent
import android.os.Bundle
import android.view.View.INVISIBLE
import android.view.View.VISIBLE
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSmoothScroller
import com.google.android.material.snackbar.Snackbar
import com.tinf18ai2.vorlesungsplan.R
import com.tinf18ai2.vorlesungsplan.backend_services.lecture_plan_modules.PlanAnalyser
import com.tinf18ai2.vorlesungsplan.backend_services.time_estimation.TimeEstimator
import com.tinf18ai2.vorlesungsplan.models.FABDataModel
import com.tinf18ai2.vorlesungsplan.models.Vorlesungstag
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*
import java.util.*
import java.util.logging.Logger
import kotlin.collections.ArrayList
import kotlin.math.max


class MainActivity : AppCompatActivity() {

    companion object {
        val LOG: Logger = Logger.getGlobal()
    }

    private lateinit var adapter: RecyclerViewAdapterVorlesungsplanWeek
    private lateinit var decorator: ItemDecorationVorlesungsplanWeek

    private lateinit var linearLayoutManager: LinearLayoutManager
    private lateinit var smoothScroller: LinearSmoothScroller

    private var networkError: Boolean = false
    private var weekShift = 0

    private lateinit var disposable: CompositeDisposable

    var woche: List<Vorlesungstag> = ArrayList()
    var currentWeek: List<Vorlesungstag> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        disposable = CompositeDisposable()

        // Layout manager and Scrolling Manager
        linearLayoutManager = LinearLayoutManager(this)
        mainRecyclerView.layoutManager = linearLayoutManager
        smoothScroller = object : LinearSmoothScroller(this) {
            override fun getVerticalSnapPreference(): Int {
                return SNAP_TO_START
            }
        }

        setSupportActionBar(toolbar)
        mainRecyclerView.visibility = INVISIBLE
        progressBar.visibility = VISIBLE

        // item decorator
        decorator = ItemDecorationVorlesungsplanWeek(64)
        mainRecyclerView.addItemDecoration(decorator)

        // adapter
        adapter = RecyclerViewAdapterVorlesungsplanWeek(
            items = ArrayList(),
            context = applicationContext
        )
        mainRecyclerView.adapter = adapter

        this.disposable.add(
            PlanAnalyser.toObservable()
            .observeOn(AndroidSchedulers.mainThread()) // Observe in mainThread for UI access
            .subscribe({
                networkError = false
                woche = it
                if (weekShift == 0) {
                    currentWeek = it
                }
                mainRecyclerView.visibility = VISIBLE
                progressBar.visibility = INVISIBLE

                adapter.items = it
                adapter.notifyDataSetChanged()
            }, {
                LOG.info("onError")
                it.printStackTrace()
                networkError = true
                makeSnackBar(getString(R.string.network_error_msg), null)
            })
        )

        fab.setOnClickListener {
            // Scroll to current day
            var day = Calendar.getInstance().get(Calendar.DAY_OF_WEEK)
            scrollToDay(day)

            if (!networkError) {
                disposable.add(
                    TimeEstimator.estimate(currentWeek)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe({
                        showTimeLeft(it)
                    },{
                        makeSnackBar(getString(R.string.network_error_msg), null)
                    })
                )
            } else {
                PlanAnalyser.analyse(weekShift)
            }
        }

        buttonWeekPrevious.setOnClickListener {
            changeWeek(-1)
        }

        buttonWeekNext.setOnClickListener {
            changeWeek(1)
        }

        buttonWeekCurrent.setOnClickListener {
            changeWeek(0)
        }

        PlanAnalyser.analyse(weekShift)
    }

    override fun onResume() {
        super.onResume()
        PlanAnalyser.analyse(weekShift)
    }

    override fun onDestroy() {
        super.onDestroy()
        disposable.dispose()
    }

    private fun changeWeek(value: Int) {
        if (value == 0) {
            weekShift = 0
        }
        weekShift += value
        PlanAnalyser.analyse(weekShift)
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
        makeSnackBar(end, timeWhen)
    }

    fun makeSnackBar(msg: String, timeWhen: FABDataModel?) {
        var snack = Snackbar.make(mainView, msg, Snackbar.LENGTH_LONG)
        if (timeWhen != null) {
            snack.setAction("SHOW") {
                val intent = Intent(this, CountdownActivity::class.java).apply {
                    putExtra("TIMESTAMP", timeWhen.timestamp)
                }
                startActivity(intent)
            }
        }
        snack.show()
    }
    
    fun scrollToDay(dayOfWeek: Int) {
        // Monday = 2
        var position = dayOfWeek - 2
        // Sunday would be negative
        position = max(position, 0)

        // Scroll to position
        LOG.info("Scrolling to day $dayOfWeek at $position")
        smoothScroller.targetPosition = position
        linearLayoutManager.startSmoothScroll(smoothScroller)
    }
}
