package com.tinf18ai2.vorlesungsplan.backend_services

import com.tinf18ai2.vorlesungsplan.models.VorlesungsplanItem
import io.reactivex.Observable
import io.reactivex.Observer

object StateData : Observable<ArrayList<VorlesungsplanItem>>() {
    private var items: ArrayList<VorlesungsplanItem> = ArrayList()



    override fun subscribeActual(observer: Observer<in ArrayList<VorlesungsplanItem>>?) {
        //TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        observer?.onNext(items)
    }

    fun updateData(data: ArrayList<VorlesungsplanItem>) {
        items = data
        this
    }
}