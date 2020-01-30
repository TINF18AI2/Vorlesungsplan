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

    /**
     * Returns a human readable formatted time span String
     * Example: 6 hours and 5 seconds
     *
     * @return human readable time span String
     */
    fun convertTimespanToString(days: Int, hours: Int, minutes: Int, seconds: Int): String
}