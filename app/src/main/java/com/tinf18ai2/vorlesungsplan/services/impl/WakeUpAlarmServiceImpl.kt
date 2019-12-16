package com.tinf18ai2.vorlesungsplan.services.impl

import android.content.Context
import android.content.Intent
import android.provider.AlarmClock
import com.tinf18ai2.vorlesungsplan.services.WakeUpAlarmService

class WakeUpAlarmServiceImpl : WakeUpAlarmService {

    override fun setupAlarm(context: Context) {
        val alarmIntent = Intent(AlarmClock.ACTION_SET_ALARM)
        alarmIntent.putExtra(AlarmClock.EXTRA_SKIP_UI, true)

        alarmIntent.putExtra(AlarmClock.EXTRA_MESSAGE, "Vorlesung")
        alarmIntent.putExtra(AlarmClock.EXTRA_HOUR, 7)
        alarmIntent.putExtra(AlarmClock.EXTRA_MINUTES, 33)

        context.startActivity(alarmIntent)
    }

}