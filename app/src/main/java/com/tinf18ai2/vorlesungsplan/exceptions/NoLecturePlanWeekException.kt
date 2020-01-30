package com.tinf18ai2.vorlesungsplan.exceptions

import java.lang.NullPointerException

class NoLecturePlanWeekException(s: String?) : NullPointerException(s)
