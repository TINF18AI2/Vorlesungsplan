package com.tinf18ai2.vorlesungsplan.services

import com.tinf18ai2.vorlesungsplan.models.Vorlesungswoche
import io.reactivex.Observable
import io.reactivex.Single

interface LecturePlanFetchService {

    /**
     * Fetches the selected week (by offset) from the remote server and returns a
     * <code>Single</code> object, which will be notified when the update is available.
     *
     * The <code>Observable</code> from <code>toObservable()</code> will also be notified.
     *
     * @see toObservable
     */
    fun fetch(weekOffset: Int): Single<Vorlesungswoche>

    /**
     * When ever <code>fetch</code> fetched a new result, this observable will be notified.
     *
     * @see fetch
     */
    fun toObservable(): Observable<Vorlesungswoche>

}