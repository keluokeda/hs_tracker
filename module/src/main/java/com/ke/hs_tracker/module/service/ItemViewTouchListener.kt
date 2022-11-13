package com.ke.hs_tracker.module.service

import android.view.MotionEvent
import android.view.View
import android.view.WindowManager


class ItemViewTouchListener(
    private val wl: WindowManager.LayoutParams,
    private val windowManager: WindowManager,
    private val rootView: View
) :
    View.OnTouchListener {
    private var x = 0
    private var y = 0
    override fun onTouch(view: View, motionEvent: MotionEvent): Boolean {
        when (motionEvent.action) {
            MotionEvent.ACTION_DOWN -> {
                x = motionEvent.rawX.toInt()
                y = motionEvent.rawY.toInt()

            }
            MotionEvent.ACTION_MOVE -> {
                val nowX = motionEvent.rawX.toInt()
                val nowY = motionEvent.rawY.toInt()
                val movedX = nowX - x
                val movedY = nowY - y
                x = nowX
                y = nowY
                wl.apply {
                    x += movedX
                    y += movedY
                }
                //更新悬浮球控件位置
                windowManager.updateViewLayout(rootView, wl)
            }
            else -> {

            }
        }
        return false
    }
}