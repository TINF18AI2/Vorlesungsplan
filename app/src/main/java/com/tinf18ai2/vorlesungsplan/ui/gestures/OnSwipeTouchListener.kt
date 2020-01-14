package com.tinf18ai2.vorlesungsplan.ui.gestures

import android.content.Context
import android.view.GestureDetector
import android.view.GestureDetector.SimpleOnGestureListener
import android.view.MotionEvent
import android.view.View
import android.view.View.OnTouchListener
import kotlin.math.abs

/**
 * A simple SwipeTouchListener for detecting left, right, top and bottom swipes.
 *
 * Source: https://stackoverflow.com/a/19506010
 */
class OnSwipeTouchListener(
    context: Context,
    val swipeLeft: () -> Unit,
    val swipeRight: () -> Unit,
    val swipeTop: () -> Unit,
    val swipeBottom: () -> Unit
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

        override fun onDown(e: MotionEvent): Boolean {
            return true
        }

        override fun onFling(
            e1: MotionEvent,
            e2: MotionEvent,
            velocityX: Float,
            velocityY: Float
        ) : Boolean {
            try {
                val diffY = e2.y - e1.y
                val diffX = e2.x - e1.x
                if (abs(diffX) > abs(diffY)) {
                    if (abs(diffX) > SWIPE_THRESHOLD && abs(velocityX) > SWIPE_VELOCITY_THRESHOLD) {
                        if (diffX > 0)
                            swipeRight()
                        else
                            swipeLeft()
                    }
                } else if (abs(diffY) > SWIPE_THRESHOLD && abs(velocityY) > SWIPE_VELOCITY_THRESHOLD) {
                    if (diffY > 0)
                        swipeBottom()
                    else
                        swipeTop()
                }
                else {
                    return false
                }
            } catch (exception: Exception) {
                exception.printStackTrace()
                return false
            }
            return true
        }
    }
}
