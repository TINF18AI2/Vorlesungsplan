package com.tinf18ai2.vorlesungsplan.services

import com.tinf18ai2.vorlesungsplan.services.impl.LecturePlanFetchServiceImpl
import com.tinf18ai2.vorlesungsplan.services.impl.LecturePlanServiceImpl
import com.tinf18ai2.vorlesungsplan.services.impl.LocaleServiceImpl
import com.tinf18ai2.vorlesungsplan.services.impl.TimeEstimationServiceImpl

object ServiceFactory {

    private lateinit var lecturePlanFetchService: LecturePlanFetchService
    private lateinit var lecturePlanService: LecturePlanService
    private lateinit var timeEstimationService: TimeEstimationService
    private lateinit var localeService: LocaleService

    fun init() {
        lecturePlanFetchService = LecturePlanFetchServiceImpl()
        lecturePlanService = LecturePlanServiceImpl()
        timeEstimationService = TimeEstimationServiceImpl()
        localeService = LocaleServiceImpl()
    }

    fun setLecturePlan(lecturePlanService: LecturePlanService) {
        this.lecturePlanService = lecturePlanService
    }

    fun getLecturePlan(): LecturePlanService {
        return lecturePlanService
    }

    fun setLecturePlanFetch(lecturePlanFetchService: LecturePlanFetchService) {
        this.lecturePlanFetchService = lecturePlanFetchService
    }

    fun getLecturePlanFetch(): LecturePlanFetchService {
        return lecturePlanFetchService
    }

    fun setTimeEstimation(timeEstimationService: TimeEstimationService) {
        this.timeEstimationService = timeEstimationService
    }

    fun getTimeEstimation(): TimeEstimationService {
        return timeEstimationService
    }

    fun setLocale(localeService: LocaleService) {
        this.localeService = localeService
    }

    fun getLocale(): LocaleService {
        return localeService
    }

}