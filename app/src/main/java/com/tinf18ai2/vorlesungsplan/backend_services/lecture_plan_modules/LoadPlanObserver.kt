package com.tinf18ai2.vorlesungsplan.backend_services.lecture_plan_modules

import com.tinf18ai2.vorlesungsplan.models.Vorlesungstag
import com.tinf18ai2.vorlesungsplan.ui.MainActivity.Companion.LOG


object LoadPlanObserver {

    private var subscribers: ArrayList<StateSubscriber> = ArrayList()
    private var values: List<Vorlesungstag>? = ArrayList()

    fun addSubscriber(subscriber: StateSubscriber) {
        LOG.info("addSubscriber")
        subscribers.add(subscriber)
    }

    fun removeSubscriber(subscriber: StateSubscriber) {
        subscribers.remove(subscriber)
    }

    fun reloadData(weekShift: Int) {
        LOG.info("reloadStarted")
        LoadData(
            weekDataCallback = object :
                WeekDataCallback {
                override fun onDataRecieved(list: List<Vorlesungstag>?) {
                    LOG.info("valuesCallback: $values")
                    values = list
                    notifyListeners()
                }
            },
            weekShift = weekShift
        ).execute()
    }

    private fun notifyListeners() {
        LOG.info("notifyListeners: $values")
        for (subscriber in subscribers) {
            subscriber.onDataRecieved(values)
        }
    }
}

interface StateSubscriber {
    fun onDataRecieved(list: List<Vorlesungstag>?)
}