package com.tinf18ai2.vorlesungsplan.services

import com.tinf18ai2.vorlesungsplan.models.FABDataModel
import io.reactivex.Single
import java.util.*

interface TimeEstimationService {

    /**
     * Estimates the time to the next lecture or the end of the current lecture by using the given
     * week.
     */
    fun estimate(): Single<FABDataModel>

    fun getTodayDateString(): String
    fun getMinutesOfToday(): Int
    fun getMinutesOfDay(beg: Date): Int

}