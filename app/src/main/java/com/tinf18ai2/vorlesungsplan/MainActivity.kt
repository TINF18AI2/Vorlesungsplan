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
    private var networkError : Boolean = false

    var woche: List<Vorlesungstag> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val linearLayoutManager = LinearLayoutManager(this)
        mainRecyclerView.layoutManager = linearLayoutManager
        setSupportActionBar(toolbar)

        fab.setOnClickListener {
            if(!networkError){
                EstimateTimeLest(woche = woche, timeResultCallback = object : TimeResultCallback {
                    override fun onFinished(time: UniAusErg) {
                        showTimeLeft(time)
                    }
                }).execute()
            }else{
                reloadViews()
            }
        }
        reloadViews()

    }

    private fun reloadViews() {
        mainRecyclerView.visibility = INVISIBLE
        progressBar.visibility = VISIBLE
        LoadData(weekDataCallback = object : WeekDataCallback {
            override fun onDataRecieved(list: List<Vorlesungstag>?) {
                if (list == null) {
                    makeSnackbar(getString(R.string.network_error_msg))
                    networkError = true
                }else {
                    networkError = false
                    woche = list
                    mainRecyclerView.visibility = VISIBLE
                    progressBar.visibility = INVISIBLE
                    adapter = VorlesungsplanAdapter(
                        items = ListItemProvider.getAllListItems(list),
                        context = applicationContext
                    )
                    mainRecyclerView.adapter = adapter
                }
            }
        }).execute()

    }

    override fun onResume() {
        super.onResume()
        reloadViews()
    }


    fun showTimeLeft(wann: UniAusErg) {
        var ende =
            if (wann.timeLeft < 0) {
                getString(R.string.no_class_msg)
            } else {
                getString(R.string.time_left_msg,wann.timeLeft,wann.name)
            }
        makeSnackbar(ende)
    }

    fun makeSnackbar(msg: String){
        Snackbar.make(mainView, msg, Snackbar.LENGTH_LONG).show()
    }
}