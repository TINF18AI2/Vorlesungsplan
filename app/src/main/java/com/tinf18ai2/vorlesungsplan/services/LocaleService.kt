package com.tinf18ai2.vorlesungsplan.services

import java.util.*

interface LocaleService {

    /**
     * Sets the locale which should be used for displaying Strings
     *
     * @param locale the new Locale
     */
    fun setDisplayLocale(locale: Locale)

    /**
     * Returns the locale which should be used for displaying Strings
     *
     * @return Locale for display
     */
    fun getDisplayLocale(): Locale

    /**
     * Returns the locale which should be used for parsing remote Strings
     *
     * @return Locale for remote parsing
     */
    fun getRemoteLocale(): Locale

}