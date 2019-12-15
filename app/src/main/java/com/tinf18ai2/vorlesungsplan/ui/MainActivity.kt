package com.tinf18ai2.vorlesungsplan.ui

import android.os.Bundle
import android.view.View.INVISIBLE
import android.view.View.VISIBLE
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.tinf18ai2.vorlesungsplan.R
import com.tinf18ai2.vorlesungsplan.models.FABDataModel
import com.tinf18ai2.vorlesungsplan.services.ServiceFactory
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*
import java.util.logging.Logger


class MainActivity : AppCompatActivity() {

    companion object {
        val LOG: Logger = Logger.getGlobal()
    }

    private lateinit var adapter: RecyclerViewAdapterVorlesungsplanWeek
    private lateinit var decorator: ItemDecorationVorlesungsplanWeek

    private var networkError: Boolean = false

    private lateinit var disposable: CompositeDisposable

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Init Services
        ServiceFactory.init()

        disposable = CompositeDisposable()

        val linearLayoutManager = LinearLayoutManager(this)
        mainRecyclerView.layoutManager = linearLayoutManager
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
            ServiceFactory.getLecturePlan()
            .toObservable()
            .observeOn(AndroidSchedulers.mainThread()) // Observe in mainThread for UI access
            .subscribe({
                networkError = false
                mainRecyclerView.visibility = VISIBLE
                progressBar.visibility = INVISIBLE

                adapter.items = it.days
                adapter.notifyDataSetChanged()
            }, {
                it.printStackTrace()
                networkError = true
                makeSnackBar(getString(R.string.network_error_msg))
            })
        )

        fab.setOnClickListener {
            if (!networkError) {
                disposable.add(
                    ServiceFactory.getTimeEstimation()
                    .estimate()
                    // Add Retry
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe({
                        showTimeLeft(it)
                    },{
                        makeSnackBar(getString(R.string.network_error_msg))
                    })
                )
            } else {
                ServiceFactory.getLecturePlan().refresh()
            }
        }

        buttonWeekPrevious.setOnClickListener {
            ServiceFactory.getLecturePlan().gotoPreviousWeek()
        }

        buttonWeekNext.setOnClickListener {
            ServiceFactory.getLecturePlan().gotoNextWeek()
        }

        buttonWeekCurrent.setOnClickListener {
            ServiceFactory.getLecturePlan().gotoCurrentWeek()
        }

        ServiceFactory.getLecturePlan().refresh()
    }

    override fun onResume() {
        super.onResume()
        ServiceFactory.getLecturePlan().refresh()
    }

    override fun onDestroy() {
        super.onDestroy()
        disposable.dispose()
    }

    private fun showTimeLeft(timeWhen: FABDataModel?) {
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

    private fun makeSnackBar(msg: String) {
        Snackbar.make(mainView, msg, Snackbar.LENGTH_LONG).show()
    }
}