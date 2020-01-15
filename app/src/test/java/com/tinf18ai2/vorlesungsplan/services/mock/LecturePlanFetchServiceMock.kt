package com.tinf18ai2.vorlesungsplan.services.mock

import com.tinf18ai2.vorlesungsplan.models.Vorlesungstag
import com.tinf18ai2.vorlesungsplan.services.LecturePlanFetchService
import io.reactivex.Observable
import io.reactivex.Single

class LecturePlanFetchServiceMock : LecturePlanFetchService {

    override fun fetch(weekOffset: Int): Single<List<Vorlesungstag>> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun toObservable(): Observable<List<Vorlesungstag>> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

}