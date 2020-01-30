package com.tinf18ai2.vorlesungsplan.services.impl

import android.content.Context
import com.tinf18ai2.vorlesungsplan.services.AndroidService

class AndroidServiceImpl : AndroidService {

    private lateinit var context: Context

    override fun setContext(context: Context) {
        this.context = context
    }

    override fun getContext(): Context {
        return context
    }

}
