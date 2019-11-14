package com.tinf18ai2.vorlesungsplan.BackendServices

import android.os.AsyncTask
import com.tinf18ai2.vorlesungsplan.Models.Vorlesungstag

class LoadData(var weekDataCallback: WeekDataCallback) :
    AsyncTask<Void, Void, List<Vorlesungstag>>() {
    override fun doInBackground(vararg p0: Void?): List<Vorlesungstag>? {
        return AsyncPlanAnalyser().analyse()
    }

    override fun onPostExecute(result: List<Vorlesungstag>?) {
        super.onPostExecute(result)
        weekDataCallback.onDataRecieved(result)
    }
}

interface WeekDataCallback {
    fun onDataRecieved(list: List<Vorlesungstag>?)
}