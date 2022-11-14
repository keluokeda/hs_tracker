package com.ke.hs_tracker.module.service

import android.view.MotionEvent
import android.view.View
import android.view.WindowManager

class ScaleTouchListener constructor(
    private val windowManager: WindowManager,
    private val rootView: View,
    private val layoutParams: WindowManager.LayoutParams
) : View.OnTouchListener {
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
                layoutParams.apply {
//                        x += movedX
//                        y += movedY
                    width += movedX
                    height += movedY

                }
                //更新悬浮球控件位置
//                windowManager.updateViewLayout(rootView, layoutParams)
                windowManager.updateViewLayout(rootView, layoutParams)
            }
            else -> {

            }
        }
        return true
    }

}