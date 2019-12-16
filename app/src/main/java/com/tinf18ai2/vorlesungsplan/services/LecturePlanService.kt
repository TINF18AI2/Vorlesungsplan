package com.tinf18ai2.vorlesungsplan.services

import com.tinf18ai2.vorlesungsplan.models.Vorlesungswoche
import io.reactivex.Observable

interface LecturePlanService {

    /**
     * Returns the current week (by current date)
     */
    fun getCurrentWeek(): Vorlesungswoche

    /**
     * Returns the week, which is currently being displayed
     */
    fun getDisplayWeek(): Vorlesungswoche

    /**
     * Refreshes the underlying data
     */
    fun refresh()

    /**
     * Moves the display week to the previous week
     */
    fun gotoPreviousWeek()

    /**
     * Moves the display week to the next week
     */
    fun gotoNextWeek()

    /**
     * Moves the display week to the current (by date) week
     */
    fun gotoCurrentWeek()

    /**
     * Returns an <ocde>Observable</code> which will be notified whenever the display week changes
     *
     * @return an <code>Observable</code> notified on display week change
     */
    fun toObservable(): Observable<Vorlesungswoche>

}