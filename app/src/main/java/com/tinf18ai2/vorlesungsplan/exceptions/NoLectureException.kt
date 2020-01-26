package com.tinf18ai2.vorlesungsplan.exceptions

import java.lang.NullPointerException

class NoLectureException(s: String?) : NullPointerException(s)
