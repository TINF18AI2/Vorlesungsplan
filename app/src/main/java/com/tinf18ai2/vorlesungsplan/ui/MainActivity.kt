package com.tinf18ai2.vorlesungsplan.ui

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View.INVISIBLE
import android.view.View.VISIBLE
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSmoothScroller
import com.google.android.material.snackbar.Snackbar
import com.tinf18ai2.vorlesungsplan.R
import com.tinf18ai2.vorlesungsplan.exceptions.NoLectureException
import com.tinf18ai2.vorlesungsplan.exceptions.NoLecturePlanWeekException
import com.tinf18ai2.vorlesungsplan.models.FABDataModel
import com.tinf18ai2.vorlesungsplan.services.ServiceFactory
import com.tinf18ai2.vorlesungsplan.ui.gestures.OnSwipeTouchListener
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*
import java.io.IOException
import java.lang.IllegalStateException
import java.lang.NullPointerException
import java.util.*
import java.util.logging.Level
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

    private lateinit var compositeDisposable: CompositeDisposable

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Init Services
        ServiceFactory.init()
        ServiceFactory.getAndroid().setContext(getApplicationContext())

        compositeDisposable = CompositeDisposable()

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

        // Observe LecturePlan changes
        this.compositeDisposable.add(
            ServiceFactory.getLecturePlan()
            .toObservable()
            .observeOn(AndroidSchedulers.mainThread()) // Observe in mainThread for UI access
            .subscribe({
                mainRecyclerView.visibility = VISIBLE
                progressBar.visibility = INVISIBLE

                adapter.items = it.days
                adapter.notifyDataSetChanged()
            }, {
                // Log and notify about the error
                LOG.log(Level.WARNING, it) { it.message }
                showSnackbarError(it)
            })
        )

        fab.setOnClickListener {
            // Scroll to current day
            val day = Calendar.getInstance().get(Calendar.DAY_OF_WEEK)
            scrollToDay(day)

            // Show time left
            this.compositeDisposable.add(
                ServiceFactory.getTimeEstimation()
                .estimate()
                .retry(3)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    showSnackbarTime(it)
                },{
                    // Log and notify about the error
                    LOG.log(Level.WARNING, it) { it.message }
                    showSnackbarError(it)
                })
            )
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

        mainRecyclerView.setOnTouchListener(
            OnSwipeTouchListener(
                this,
                {   ServiceFactory.getLecturePlan().gotoNextWeek()
                    true },
                {   ServiceFactory.getLecturePlan().gotoPreviousWeek()
                    true },
                null, null)
        )

        ServiceFactory.getLecturePlan().refresh()
    }

    override fun onResume() {
        super.onResume()
        ServiceFactory.getLecturePlan().refresh()
    }

    override fun onDestroy() {
        super.onDestroy()
        compositeDisposable.dispose()
    }

    private fun showSnackbarTime(timeWhen: FABDataModel) {
        // Convert time span to human readable string
        val timespan = ServiceFactory.getLocale().convertTimespanToString(
            timeWhen.days,
            timeWhen.hours,
            timeWhen.mins,
            0
        )

        // Compose message
        val message = when(timeWhen.to) {
            true -> getString(R.string.msg_info_lecture_end, timespan, timeWhen.name)
            else -> getString(R.string.msg_info_lecture_start, timespan, timeWhen.name)
        }

        // Create new Snackbar
        val snackbar = Snackbar.make(mainView, message, Snackbar.LENGTH_LONG)

        // Add SHOW button if applicable
        snackbar.setAction("SHOW") {
            var intent = Intent(this, CountdownActivity::class.java)
            intent.putExtra("TIMESTAMP", timeWhen.timestamp)
            startActivity(intent)
        }

        // Show Snackbar
        snackbar.show()
    }

    private fun showSnackbarError(throwable: Throwable) {
        // Choose message
        val message = when(throwable) {
            is NoLectureException -> getText(R.string.msg_error_no_lecture)
            is NoLecturePlanWeekException -> getText(R.string.msg_error_no_lecture_plan)
            is IOException -> getText(R.string.msg_error_network)
            else -> getText(R.string.msg_error_ambiguous)
        }
        // Show Snackbar
        val snackbar = Snackbar.make(mainView, message, Snackbar.LENGTH_LONG)
        snackbar.show()
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
