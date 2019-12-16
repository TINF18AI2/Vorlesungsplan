package com.tinf18ai2.vorlesungsplan.services.impl

import com.tinf18ai2.vorlesungsplan.services.LocaleService
import java.util.*

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

}
