package com.example.rsshool2021_android_task_pomodoro.stopwatch

import android.os.CountDownTimer

class Stopwatch(
    val startPeriod: Long,
    val id: Int = nextId++,
    var currentMs: Long = startPeriod,
    var isStarted: Boolean = false
) {
    private var countDown: CountDownTimer? = null

    val progress: Long
        get() = currentMs
    val text: String
        get() = getTimerText()

    var tickCallback: (() -> Unit)? = null
    var stateCallback: (() -> Unit)? = null

    fun start() {
        if (!isStarted) {
            isStarted = true

            countDown = setCountDownTimer(currentMs)
            countDown?.start()

            stateCallback?.invoke()
        }
    }

    fun stop() {
        if (isStarted) {
            isStarted = false
            countDown?.cancel()

            stateCallback?.invoke()
        }
    }

    fun reset() {
        isStarted = false
        currentMs = startPeriod
        countDown?.cancel()

        stateCallback?.invoke()
    }

    private fun setCountDownTimer(ms: Long): CountDownTimer {
        return object : CountDownTimer(ms, TICK) {

            override fun onTick(millisUntilFinished: Long) {
                currentMs = millisUntilFinished
                if (currentMs > startPeriod) currentMs = startPeriod
                tickCallback?.invoke()
            }

            override fun onFinish() {
                currentMs = 0
                isStarted = false
                stateCallback?.invoke()
            }
        }
    }

    private fun getTimerText(): String {
        val fakeMs = if (currentMs <= 0L) 0L else { if (currentMs + 1000 > startPeriod) startPeriod else currentMs + 1000 }

        val hour = fakeMs / 1000 / 3600
        val min = fakeMs / 1000 % 3600 / 60
        val sec = fakeMs / 1000 % 60

        return "${hour.twoDigits()}:${min.twoDigits()}:${sec.twoDigits()}"
    }

    private fun Long.twoDigits(): String = this.toString().padStart(2, '0')


    private companion object {
        private const val TICK = 100L
        private var nextId = 0
    }
}
