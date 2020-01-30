package com.tinf18ai2.vorlesungsplan.services

import com.tinf18ai2.vorlesungsplan.exceptions.NoLectureException
import com.tinf18ai2.vorlesungsplan.exceptions.NoLecturePlanWeekException
import com.tinf18ai2.vorlesungsplan.models.FABDataModel
import io.reactivex.Single
import java.io.IOException
import java.util.*

interface TimeEstimationService {

    /**
     * Estimates the time to the next lecture or the end of the current lecture by using the given
     * week.
     *
     * @throws NoLectureException If there is no current or next lecture found
     * @throws NoLecturePlanWeekException If there is currently no lecture plan week available
     * @throws IOException If the download is not possible
     */
    @Throws(NoLectureException::class, NoLecturePlanWeekException::class, IOException::class)
    fun estimate(): Single<FABDataModel>

    fun getTodayDateString(): String
    fun getMinutesOfToday(): Int
    fun getMinutesOfDay(beg: Date): Int

}