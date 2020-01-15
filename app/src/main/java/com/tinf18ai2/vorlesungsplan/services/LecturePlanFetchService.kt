package com.tinf18ai2.vorlesungsplan.services

import com.tinf18ai2.vorlesungsplan.models.Vorlesungswoche
import io.reactivex.Single

interface LecturePlanFetchService {

    /**
     * Fetches the selected week (by offset) from the remote server and returns a
     * <code>Single</code> object, which will be notified when the update is available.
     */
    fun fetch(weekOffset: Int): Single<Vorlesungswoche>

}