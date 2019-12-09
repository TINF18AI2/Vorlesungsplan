package com.tinf18ai2.vorlesungsplan.backend_services.lecture_plan_modules

import android.os.AsyncTask
import com.tinf18ai2.vorlesungsplan.models.Vorlesungstag

class LoadData(var weekDataCallback: WeekDataCallback, var weekShift: Int) :
    AsyncTask<Void, Void, List<Vorlesungstag>>() {

    override fun doInBackground(vararg p0: Void?): List<Vorlesungstag>? {
        return PlanAnalyser()
            .analyse(weekShift)
    }

    override fun onPostExecute(result: List<Vorlesungstag>?) {
        super.onPostExecute(result)
        weekDataCallback.onDataRecieved(result)
    }
}

interface WeekDataCallback {
    fun onDataRecieved(list: List<Vorlesungstag>?)
}