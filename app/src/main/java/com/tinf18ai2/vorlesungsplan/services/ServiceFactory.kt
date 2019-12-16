package com.tinf18ai2.vorlesungsplan.services

import android.content.Context
import com.tinf18ai2.vorlesungsplan.services.impl.*

object ServiceFactory {

    private lateinit var lecturePlanFetchService: LecturePlanFetchService
    private lateinit var lecturePlanService: LecturePlanService
    private lateinit var timeEstimationService: TimeEstimationService
    private lateinit var localeService: LocaleService
    private lateinit var wakeUpAlarmService: WakeUpAlarmService

    fun init() {
        lecturePlanFetchService = LecturePlanFetchServiceImpl()
        lecturePlanService = LecturePlanServiceImpl()
        timeEstimationService = TimeEstimationServiceImpl()
        localeService = LocaleServiceImpl()
        wakeUpAlarmService = WakeUpAlarmServiceImpl()
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

    fun setWakupAlarm(wakeUpAlarmService: WakeUpAlarmService) {
        this.wakeUpAlarmService = wakeUpAlarmService
    }

    fun getWakeUpAlarm(): WakeUpAlarmService {
        return wakeUpAlarmService
    }

}