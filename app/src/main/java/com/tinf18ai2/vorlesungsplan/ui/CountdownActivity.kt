package com.tinf18ai2.vorlesungsplan.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.tinf18ai2.vorlesungsplan.R
import io.reactivex.Flowable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import kotlinx.android.synthetic.main.activity_countdown.*
import java.util.concurrent.TimeUnit

class CountdownActivity : AppCompatActivity() {

    private lateinit var disposable: Disposable
    private var timestamp: Long = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_countdown)

        timestamp = intent.getLongExtra("TIMESTAMP", 1)

        // Using a prime number as period for nicer visuals
        disposable = Flowable.interval(13, TimeUnit.MILLISECONDS)
            .map { calculateTime() }
            .takeUntil { it <= 0 }
            .map { toTimeLeft(it) }
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe( { // onNext
                textViewCountdown.text = it
            }, { // onError
                textViewCountdown.text = toTimeLeft(0)
            }, { // onComplete
                textViewCountdown.text = toTimeLeft(0)
            })
    }

    override fun onDestroy() {
        super.onDestroy()
        disposable.dispose()
    }

    private fun calculateTime(): Long {
        // A VERY BAD HACK, as the given timestamp is not correct
        var currentTime = System.currentTimeMillis() % (24*60*60*1000)
        return timestamp - currentTime
    }

    private fun toTimeLeft(time: Long): String {
        var millis = time

        val hours = TimeUnit.MILLISECONDS.toHours(millis)
        millis -= TimeUnit.HOURS.toMillis(hours)
        val minutes = TimeUnit.MILLISECONDS.toMinutes(millis)
        millis -= TimeUnit.MINUTES.toMillis(minutes)
        val seconds = TimeUnit.MILLISECONDS.toSeconds(millis)
        millis -= TimeUnit.SECONDS.toMillis(seconds)

        return String.format(
            "%02d:%02d:%02d.%03d",
            hours,
            minutes,
            seconds,
            millis
        )
    }
}
