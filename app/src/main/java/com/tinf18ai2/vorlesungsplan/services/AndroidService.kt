package com.tinf18ai2.vorlesungsplan.services

import android.content.Context

interface AndroidService {

    /**
     * Returns app Context
     */
    fun getContext(): Context

    /**
     * Sets the app Context
     */
    fun setContext(context: Context)

}