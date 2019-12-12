package com.tinf18ai2.vorlesungsplan.ui

import android.content.res.Configuration
import android.os.Bundle
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.view.WindowManager.*
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
    private var showMs: Boolean = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Remove status bar
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        window.setFlags(LayoutParams.FLAG_FULLSCREEN, LayoutParams.FLAG_FULLSCREEN)
        // Set Layout
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

        // Add Listener to fullscreen button
        buttonToPictureInPicture.setOnClickListener {
            enterPictureInPictureMode()
        }
    }

    override fun onPictureInPictureModeChanged(
        isInPictureInPictureMode: Boolean,
        newConfig: Configuration
    ) {
        if (isInPictureInPictureMode) {
            showMs = false
            buttonToPictureInPicture.visibility = View.GONE
            val timespan = Math.max(calculateTime(), 0)
            textViewCountdown.text = toTimeLeft(timespan)
        } else {
            showMs = true
            buttonToPictureInPicture.visibility = View.VISIBLE
            val timespan = Math.max(calculateTime(), 0)
            textViewCountdown.text = toTimeLeft(timespan)
        }
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

        var format = when(showMs)
        {
            true -> "%01d:%02d:%02d.%03d"
            false -> "%01d:%02d:%02d"
        }

        return String.format(
            format,
            hours,
            minutes,
            seconds,
            millis
        )
    }

}
