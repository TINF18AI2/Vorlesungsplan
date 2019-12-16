package com.tinf18ai2.vorlesungsplan.services.impl

import com.tinf18ai2.vorlesungsplan.models.Vorlesungswoche
import com.tinf18ai2.vorlesungsplan.services.LecturePlanService
import com.tinf18ai2.vorlesungsplan.services.ServiceFactory
import io.reactivex.Observable
import io.reactivex.subjects.BehaviorSubject

class LecturePlanServiceImpl : LecturePlanService {

    private val input = BehaviorSubject.create<Int>()
    private val output = BehaviorSubject.create<Vorlesungswoche>()

    private lateinit var currentWeek: Vorlesungswoche

    private lateinit var displayWeek: Vorlesungswoche
    private var displayWeekOffset = 0

    init {
        input
            .flatMapSingle{ ServiceFactory.getLecturePlanFetch().fetch(it) }
            .doOnNext{
                currentWeek = it

                // If the fetched week is the current week, save the week
                if(it.weekOffset == 0) {
                    currentWeek = it
                }
            }
            .subscribe(output)
    }

    override fun getCurrentWeek(): Vorlesungswoche {
        return currentWeek
    }

    override fun getDisplayWeek(): Vorlesungswoche {
        return displayWeek
    }

    override fun refresh() {
        input.onNext(displayWeekOffset)
    }

    override fun gotoPreviousWeek() {
        displayWeekOffset--
        input.onNext(displayWeekOffset)
    }

    override fun gotoNextWeek() {
        displayWeekOffset++
        input.onNext(displayWeekOffset)
    }

    override fun gotoCurrentWeek() {
        displayWeekOffset = 0
        input.onNext(displayWeekOffset)
    }

    override fun toObservable(): Observable<Vorlesungswoche> {
        return output
    }

}