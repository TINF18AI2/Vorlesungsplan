package com.tinf18ai2.vorlesungsplan.backend_services.time_estimation

import android.os.AsyncTask
import com.tinf18ai2.vorlesungsplan.models.Vorlesungstag

class EstimateTimeLeft(val woche: List<Vorlesungstag>, val timeResultCallback: TimeResultCallback) :
    AsyncTask<Void, Void, UniAusErg?>() {
    override fun doInBackground(vararg p0: Void?): UniAusErg? {
        return TimeEstimator()
            .getFABData(woche)
    }

    override fun onPostExecute(result: UniAusErg?) {
        super.onPostExecute(result)
        timeResultCallback.onFinished(result)
    }
}

interface TimeResultCallback {
    fun onFinished(time: UniAusErg?)
}