package com.tinf18ai2.vorlesungsplan

import android.os.AsyncTask

class EstimateTimeLest(val woche: List<Vorlesungstag>, val timeResultCallback: TimeResultCallback) :
    AsyncTask<Void, Void, UniAusErg>() {
    override fun doInBackground(vararg p0: Void?): UniAusErg {
        return TimeUntilUniEnd().getTimeLeft(woche)
    }

    override fun onPostExecute(result: UniAusErg) {
        super.onPostExecute(result)
        if (result != null) {
            timeResultCallback.onFinished(result)
        }
    }
}

interface TimeResultCallback {
    fun onFinished(time: UniAusErg)
}