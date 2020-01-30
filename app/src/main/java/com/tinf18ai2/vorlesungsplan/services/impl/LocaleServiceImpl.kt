package com.tinf18ai2.vorlesungsplan.services.impl

import com.tinf18ai2.vorlesungsplan.R
import com.tinf18ai2.vorlesungsplan.services.LocaleService
import com.tinf18ai2.vorlesungsplan.services.ServiceFactory
import java.util.*
import kotlin.collections.ArrayList

class LocaleServiceImpl : LocaleService {

    private var display = Locale.GERMANY

    override fun setDisplayLocale(locale: Locale) {
        display = locale
    }

    override fun getDisplayLocale(): Locale {
        return display
    }

    override fun getRemoteLocale(): Locale {
        return Locale.GERMANY
    }

    override fun convertTimespanToString(days: Int, hours: Int, minutes: Int, seconds: Int): String {
        val strings: ArrayList<CharSequence> = ArrayList()

        strings.add(
            when(days) {
                0 -> ServiceFactory.getAndroid().getContext().getString(R.string.str_timespan_days_none)
                1 -> ServiceFactory.getAndroid().getContext().getString(R.string.str_timespan_days_singular, days)
                else -> ServiceFactory.getAndroid().getContext().getString(R.string.str_timespan_days_plural, days)
            }
        )

        strings.add(
            when(hours) {
                0 -> ServiceFactory.getAndroid().getContext().getString(R.string.str_timespan_hours_none)
                1 -> ServiceFactory.getAndroid().getContext().getString(R.string.str_timespan_hours_singular, hours)
                else -> ServiceFactory.getAndroid().getContext().getString(R.string.str_timespan_hours_plural, hours)
            }
        )

        strings.add(
            when(minutes) {
                0 -> ServiceFactory.getAndroid().getContext().getString(R.string.str_timespan_minutes_none)
                1 -> ServiceFactory.getAndroid().getContext().getString(R.string.str_timespan_minutes_singular, minutes)
                else -> ServiceFactory.getAndroid().getContext().getString(R.string.str_timespan_minutes_plural, minutes)
            }
        )

        strings.add(
            when(seconds) {
                0 -> ServiceFactory.getAndroid().getContext().getString(R.string.str_timespan_seconds_none)
                1 -> ServiceFactory.getAndroid().getContext().getString(R.string.str_timespan_seconds_singular, seconds)
                else -> ServiceFactory.getAndroid().getContext().getString(R.string.str_timespan_seconds_plural, seconds)
            }
        )

        strings.removeIf { t: CharSequence -> t.isEmpty() }

        return when(strings.size) {
            0 -> ServiceFactory.getAndroid().getContext().getString(R.string.str_timespan_none)
            1 -> strings.first().toString()
            else -> {
                strings.subList(0, strings.size - 1)
                    .joinToString(ServiceFactory.getAndroid().getContext().getString(R.string.str_timespan_seperator)) +
                        ServiceFactory.getAndroid().getContext().getString(R.string.str_timespan_seperator_last) + strings.last()
            }
        }
    }

}
