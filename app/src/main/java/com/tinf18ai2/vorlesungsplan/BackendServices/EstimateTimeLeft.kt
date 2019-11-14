package com.tinf18ai2.vorlesungsplan.BackendServices

import android.os.AsyncTask
import com.tinf18ai2.vorlesungsplan.Models.Vorlesungstag

class EstimateTimeLest(val woche: List<Vorlesungstag>, val timeResultCallback: TimeResultCallback) :
    AsyncTask<Void, Void, UniAusErg>() {
    override fun doInBackground(vararg p0: Void?): UniAusErg {
        return TimeUntilUniEnd().getTimeLeft(woche)
    }

    override fun onPostExecute(result: UniAusErg) {
        super.onPostExecute(result)
        timeResultCallback.onFinished(result)
    }
}

interface TimeResultCallback {
    fun onFinished(time: UniAusErg)
}