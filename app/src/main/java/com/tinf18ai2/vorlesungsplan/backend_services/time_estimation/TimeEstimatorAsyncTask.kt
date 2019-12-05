package com.tinf18ai2.vorlesungsplan.backend_services.time_estimation

import android.os.AsyncTask
import com.tinf18ai2.vorlesungsplan.models.FABDataModel
import com.tinf18ai2.vorlesungsplan.models.Vorlesungstag

class EstimateTimeLeft(val woche: List<Vorlesungstag>, val timeResultCallback: TimeResultCallback) :
    AsyncTask<Void, Void, FABDataModel?>() {
    override fun doInBackground(vararg p0: Void?): FABDataModel? {
        return TimeEstimator()
            .getFABData(woche)
    }

    override fun onPostExecute(result: FABDataModel?) {
        super.onPostExecute(result)
        timeResultCallback.onFinished(result)
    }
}

interface TimeResultCallback {
    fun onFinished(time: FABDataModel?)
}