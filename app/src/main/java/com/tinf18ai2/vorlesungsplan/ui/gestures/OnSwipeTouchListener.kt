package com.tinf18ai2.vorlesungsplan.ui.gestures

import android.content.Context
import android.view.GestureDetector
import android.view.GestureDetector.SimpleOnGestureListener
import android.view.MotionEvent
import android.view.View
import android.view.View.OnTouchListener
import com.tinf18ai2.vorlesungsplan.ui.MainActivity
import kotlin.math.abs
import kotlin.math.max

/**
 * A simple SwipeTouchListener for detecting left, right, top and bottom swipes.
 *
 * Source: https://stackoverflow.com/a/19506010
 */
class OnSwipeTouchListener(
    context: Context,
    val swipeLeft: (() -> Boolean)?,
    val swipeRight: (() -> Boolean)?,
    val swipeTop: (() -> Boolean)?,
    val swipeBottom: (() -> Boolean)?
) :
    OnTouchListener {

    private val gestureDetector: GestureDetector

    init {
        gestureDetector = GestureDetector(context, GestureListener())
    }

    override fun onTouch(v: View, event: MotionEvent): Boolean {
        return gestureDetector.onTouchEvent(event)
    }

    private companion object {
        const val SWIPE_THRESHOLD = 100
        const val SWIPE_VELOCITY_THRESHOLD = 100
    }

    private inner class GestureListener : SimpleOnGestureListener() {

        override fun onDown(motionEvent: MotionEvent): Boolean {
            return true
        }

        override fun onFling(
            motionEvent1: MotionEvent,
            motionEvent2: MotionEvent,
            velocityX: Float,
            velocityY: Float
        ) : Boolean {
            val diffX = abs(motionEvent2.x - motionEvent1.x)
            val diffY = abs(motionEvent2.y - motionEvent1.y)

            // Horizontal motion
            if (diffX > diffY) {
                // Swipe long enough
                if (diffX < SWIPE_THRESHOLD)
                    return false
                // Swipe fast enough
                if (abs(velocityX) < SWIPE_VELOCITY_THRESHOLD)
                    return false

                return if (diffX > 0)
                    swipeRight?.invoke() ?: false
                else
                    swipeLeft?.invoke() ?: false
            }
            // Vertical motion
            else {
                // Swipe long enough
                if (diffY < SWIPE_THRESHOLD)
                    return false
                // Swipe fast enough
                if (abs(velocityY) < SWIPE_VELOCITY_THRESHOLD)
                    return false

                return if (diffY > 0)
                    swipeBottom?.invoke() ?: false
                else
                    swipeTop?.invoke() ?: false
            }
        }
    }
}
